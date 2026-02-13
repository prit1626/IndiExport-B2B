package com.IndiExport.backend.service.currency;

import com.IndiExport.backend.exception.InvalidMoneyAmountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Converts amounts from INR (paise) to any supported target currency (minor units).
 *
 * Conversion math explained:
 * ─────────────────────────
 * Given:
 *   amountPaise     = price in Indian paise (long)
 *   rateMicros      = (1 INR → X target) * 1_000_000
 *   inrMultiplier   = 100 (paise per INR)
 *   targetMultiplier = e.g. 100 for USD, 1 for JPY, 1000 for KWD
 *
 * Step 1: Convert paise to major INR:     majorINR  = amountPaise / 100
 * Step 2: Apply exchange rate:            majorTarget = majorINR * rate
 * Step 3: Convert to target minor units:  minorTarget = majorTarget * targetMultiplier
 *
 * Combined (integer math to avoid floating-point):
 *   convertedMinor = (amountPaise * rateMicros * targetMultiplier) / (inrMultiplier * 1_000_000)
 *                  = (amountPaise * rateMicros * targetMultiplier) / 100_000_000
 *
 * We use Math.round on intermediate double to handle any remainder correctly.
 */
@Service
public class CurrencyConversionService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyConversionService.class);

    private final ExchangeRateCacheService cacheService;

    public CurrencyConversionService(ExchangeRateCacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * Convert an INR paise amount to the target currency's minor units.
     *
     * @param amountPaise    amount in INR paise (must be positive)
     * @param targetCurrency ISO 4217 code (e.g. "USD", "JPY")
     * @return ConversionResult with all details
     */
    public ConversionResult convertFromINR(long amountPaise, String targetCurrency) {
        if (amountPaise <= 0) {
            throw new InvalidMoneyAmountException(amountPaise);
        }

        String currency = CurrencyMetadata.validateAndNormalize(targetCurrency);

        // Same-currency shortcut
        if ("INR".equals(currency)) {
            return new ConversionResult(
                    amountPaise, "INR",
                    amountPaise, "INR",
                    1_000_000L,     // 1:1 rate
                    Instant.now(),
                    "identity"
            );
        }

        ExchangeRateCacheService.CachedRate cached = cacheService.getRate(currency);
        long rateMicros = cached.rateMicros();
        long targetMultiplier = CurrencyMetadata.getMinorUnitMultiplier(currency);
        long inrMultiplier = CurrencyMetadata.getMinorUnitMultiplier("INR"); // 100

        // Integer math: (amountPaise * rateMicros * targetMultiplier) / (inrMultiplier * 1_000_000)
        // Use double for intermediate to avoid long overflow for very large amounts
        double numerator = (double) amountPaise * rateMicros * targetMultiplier;
        double denominator = (double) inrMultiplier * 1_000_000L;
        long convertedMinor = Math.round(numerator / denominator);

        log.debug("Converted {} INR paise → {} {} minor | rate micros: {} | target multiplier: {}",
                amountPaise, convertedMinor, currency, rateMicros, targetMultiplier);

        return new ConversionResult(
                amountPaise,
                "INR",
                convertedMinor,
                currency,
                rateMicros,
                cached.fetchedAt(),
                cached.providerName()
        );
    }

    /**
     * Immutable result of a currency conversion.
     */
    public record ConversionResult(
            long baseAmountMinor,
            String baseCurrency,
            long convertedAmountMinor,
            String targetCurrency,
            long exchangeRateMicros,
            Instant rateTimestamp,
            String providerName
    ) {}
}
