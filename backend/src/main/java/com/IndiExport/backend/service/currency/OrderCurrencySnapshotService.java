package com.IndiExport.backend.service.currency;

import com.IndiExport.backend.entity.Order;
import com.IndiExport.backend.entity.OrderCurrencySnapshot;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.OrderCurrencySnapshotRepository;
import com.IndiExport.backend.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Creates and retrieves immutable currency snapshots for orders.
 * Once a snapshot is created at checkout, it MUST NOT be modified.
 */
@Service
public class OrderCurrencySnapshotService {

    private static final Logger log = LoggerFactory.getLogger(OrderCurrencySnapshotService.class);

    private final OrderCurrencySnapshotRepository snapshotRepository;
    private final OrderRepository orderRepository;
    private final CurrencyConversionService conversionService;

    public OrderCurrencySnapshotService(OrderCurrencySnapshotRepository snapshotRepository,
                                        OrderRepository orderRepository,
                                        CurrencyConversionService conversionService) {
        this.snapshotRepository = snapshotRepository;
        this.orderRepository = orderRepository;
        this.conversionService = conversionService;
    }

    /**
     * Lock the current exchange rate for an order at checkout time.
     * Creates an immutable snapshot row.
     *
     * @param orderId         the order to lock the rate for
     * @param baseTotalPaise  order total in INR paise
     * @param buyerCurrency   buyer's preferred currency (e.g. "USD")
     * @return the created snapshot
     */
    @Transactional
    public OrderCurrencySnapshot lockRateForOrder(UUID orderId, long baseTotalPaise, String buyerCurrency) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));

        // Check if snapshot already exists (idempotency)
        if (snapshotRepository.findByOrderId(orderId).isPresent()) {
            log.warn("Currency snapshot already exists for order {}, returning existing", orderId);
            return snapshotRepository.findByOrderId(orderId).get();
        }

        CurrencyConversionService.ConversionResult result =
                conversionService.convertFromINR(baseTotalPaise, buyerCurrency);

        OrderCurrencySnapshot snapshot = new OrderCurrencySnapshot();
        snapshot.setOrder(order);
        snapshot.setBaseCurrency("INR");
        snapshot.setBuyerCurrency(result.targetCurrency());
        snapshot.setExchangeRateMicros(result.exchangeRateMicros());
        snapshot.setRateTimestamp(result.rateTimestamp());
        snapshot.setProviderName(result.providerName());
        snapshot.setBaseTotalPaise(baseTotalPaise);
        snapshot.setConvertedTotalMinor(result.convertedAmountMinor());
        snapshot.setCreatedAt(Instant.now());

        OrderCurrencySnapshot saved = snapshotRepository.save(snapshot);
        log.info("Locked currency rate for order {}: {} INR paise → {} {} minor | rate micros: {}",
                orderId, baseTotalPaise, result.convertedAmountMinor(),
                result.targetCurrency(), result.exchangeRateMicros());

        return saved;
    }

    /**
     * Retrieve the currency snapshot for an order.
     */
    @Transactional(readOnly = true)
    public OrderCurrencySnapshot getSnapshotByOrderId(UUID orderId) {
        return snapshotRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "OrderCurrencySnapshot", orderId.toString()));
    }
}
