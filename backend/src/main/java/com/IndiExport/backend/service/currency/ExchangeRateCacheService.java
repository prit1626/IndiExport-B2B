package com.IndiExport.backend.service.currency;

import com.IndiExport.backend.exception.ExchangeRateUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe in-memory cache for exchange rates.
 *
 * Design:
 * - ConcurrentHashMap for O(1) reads
 * - Per-currency ReentrantLock to prevent thundering herd on cache miss
 *   (only ONE thread fetches while others wait on the same currency lock)
 * - TTL-based expiration (default 12 hours)
 * - Falls back to stale cached rate if external API fails and cache exists
 */
@Service
public class ExchangeRateCacheService {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateCacheService.class);

    private final FrankfurterExchangeRateClient exchangeRateClient;
    private final long ttlHours;

    private final ConcurrentHashMap<String, CachedRate> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public ExchangeRateCacheService(
            FrankfurterExchangeRateClient exchangeRateClient,
            @Value("${currency.cache.ttl-hours:12}") long ttlHours) {
        this.exchangeRateClient = exchangeRateClient;
        this.ttlHours = ttlHours;
    }

    /**
     * Get the exchange rate (as micros) for INR → targetCurrency.
     * Returns cached value if fresh, otherwise fetches and caches.
     *
     * @return CachedRate with rateMicros, fetchedAt, and providerName
     */
    public CachedRate getRate(String targetCurrency) {
        String key = targetCurrency.toUpperCase();

        // Fast path: return cached value if still fresh
        CachedRate cached = cache.get(key);
        if (cached != null && !isExpired(cached)) {
            log.debug("Cache hit for {}: rateMicros={}", key, cached.rateMicros());
            return cached;
        }

        // Slow path: acquire per-currency lock and fetch
        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
        lock.lock();
        try {
            // Double-check: another thread may have refreshed while we waited
            cached = cache.get(key);
            if (cached != null && !isExpired(cached)) {
                log.debug("Cache hit after lock for {}: rateMicros={}", key, cached.rateMicros());
                return cached;
            }

            // Fetch fresh rate
            try {
                double rawRate = exchangeRateClient.fetchRate(key);
                long rateMicros = Math.round(rawRate * 1_000_000L);
                CachedRate fresh = new CachedRate(
                        rateMicros,
                        Instant.now(),
                        exchangeRateClient.getProviderName()
                );
                cache.put(key, fresh);
                log.info("Cached fresh rate for {}: rateMicros={}", key, rateMicros);
                return fresh;

            } catch (Exception fetchError) {
                // Fallback: return stale cached rate if available
                if (cached != null) {
                    log.warn("API fetch failed for {}, using stale cache (age: {} hours): {}",
                            key,
                            ChronoUnit.HOURS.between(cached.fetchedAt(), Instant.now()),
                            fetchError.getMessage());
                    return cached;
                }
                // No cache at all — propagate error
                throw new ExchangeRateUnavailableException(key, fetchError);
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean isExpired(CachedRate rate) {
        return Instant.now().isAfter(rate.fetchedAt().plus(ttlHours, ChronoUnit.HOURS));
    }

    /**
     * Immutable cached rate record.
     */
    public record CachedRate(
            long rateMicros,      // rate * 1_000_000 (e.g. 0.01195 → 11950)
            Instant fetchedAt,
            String providerName
    ) {}
}
