package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Shipping cost quote snapshot for an order.
 * Stores the full breakdown so the calculation is auditable and immutable.
 */
@Entity
@Table(name = "shipping_quotes", indexes = {
        @Index(name = "idx_shipping_quotes_order_id", columnList = "order_id")
})
public class ShippingQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShippingMode mode;

    @Column(nullable = false, length = 2)
    private String destinationCountry;

    @Column(nullable = false)
    private long totalWeightGrams;

    /** Max of actual weight vs volumetric weight. */
    @Column(nullable = false)
    private long chargeableWeightGrams = 0;

    @Column(nullable = false)
    private long shippingCostPaise;

    @Column(nullable = false)
    private int estimatedDeliveryDaysMin;

    @Column(nullable = false)
    private int estimatedDeliveryDaysMax;

    /** SELLER_TEMPLATE or GLOBAL_CALCULATOR */
    @Column(nullable = false, length = 50)
    private String quoteSource = "GLOBAL_CALCULATOR";

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public ShippingQuote() {}

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public ShippingMode getMode() { return mode; }
    public void setMode(ShippingMode mode) { this.mode = mode; }

    public String getDestinationCountry() { return destinationCountry; }
    public void setDestinationCountry(String destinationCountry) { this.destinationCountry = destinationCountry; }

    public long getTotalWeightGrams() { return totalWeightGrams; }
    public void setTotalWeightGrams(long totalWeightGrams) { this.totalWeightGrams = totalWeightGrams; }

    public long getChargeableWeightGrams() { return chargeableWeightGrams; }
    public void setChargeableWeightGrams(long chargeableWeightGrams) { this.chargeableWeightGrams = chargeableWeightGrams; }

    public long getShippingCostPaise() { return shippingCostPaise; }
    public void setShippingCostPaise(long shippingCostPaise) { this.shippingCostPaise = shippingCostPaise; }

    public int getEstimatedDeliveryDaysMin() { return estimatedDeliveryDaysMin; }
    public void setEstimatedDeliveryDaysMin(int estimatedDeliveryDaysMin) { this.estimatedDeliveryDaysMin = estimatedDeliveryDaysMin; }

    public int getEstimatedDeliveryDaysMax() { return estimatedDeliveryDaysMax; }
    public void setEstimatedDeliveryDaysMax(int estimatedDeliveryDaysMax) { this.estimatedDeliveryDaysMax = estimatedDeliveryDaysMax; }

    public String getQuoteSource() { return quoteSource; }
    public void setQuoteSource(String quoteSource) { this.quoteSource = quoteSource; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
