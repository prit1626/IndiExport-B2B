package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable snapshot of the exchange rate locked at checkout.
 * Once created, this row MUST NOT be updated — it is an audit record.
 *
 * exchangeRateMicros stores the rate multiplied by 1_000_000 to avoid
 * floating-point drift. For example, 1 INR = 0.01195 USD is stored as 11950.
 */
@Entity
@Table(name = "order_currency_snapshot", indexes = {
        @Index(name = "idx_ocs_order_id", columnList = "order_id")
})
public class OrderCurrencySnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, length = 3)
    private String baseCurrency = "INR";

    @Column(nullable = false, length = 3)
    private String buyerCurrency;

    /**
     * Exchange rate as micros (rate * 1_000_000).
     * Example: 1 INR = 0.01195 USD → exchangeRateMicros = 11950
     */
    @Column(nullable = false)
    private long exchangeRateMicros;

    @Column(nullable = false)
    private Instant rateTimestamp;

    @Column(nullable = false, length = 50)
    private String providerName;

    @Column(nullable = false)
    private long baseTotalPaise;

    @Column(nullable = false)
    private long convertedTotalMinor;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public OrderCurrencySnapshot() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }

    public String getBuyerCurrency() { return buyerCurrency; }
    public void setBuyerCurrency(String buyerCurrency) { this.buyerCurrency = buyerCurrency; }

    public long getExchangeRateMicros() { return exchangeRateMicros; }
    public void setExchangeRateMicros(long exchangeRateMicros) { this.exchangeRateMicros = exchangeRateMicros; }

    public Instant getRateTimestamp() { return rateTimestamp; }
    public void setRateTimestamp(Instant rateTimestamp) { this.rateTimestamp = rateTimestamp; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public long getBaseTotalPaise() { return baseTotalPaise; }
    public void setBaseTotalPaise(long baseTotalPaise) { this.baseTotalPaise = baseTotalPaise; }

    public long getConvertedTotalMinor() { return convertedTotalMinor; }
    public void setConvertedTotalMinor(long convertedTotalMinor) { this.convertedTotalMinor = convertedTotalMinor; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
