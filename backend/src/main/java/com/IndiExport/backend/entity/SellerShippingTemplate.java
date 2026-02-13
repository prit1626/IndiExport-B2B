package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.Instant;
import java.util.UUID;

/**
 * Seller-defined shipping rate template.
 * Matched by destination (country or zone) + shipping mode + weight range.
 * If no template matches, the global calculator is used as fallback.
 */
@Entity
@Table(name = "seller_shipping_templates", indexes = {
        @Index(name = "idx_sst_seller_id", columnList = "seller_id"),
        @Index(name = "idx_sst_destination", columnList = "destination_type, destination_value"),
        @Index(name = "idx_sst_mode", columnList = "shipping_mode")
})
public class SellerShippingTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerProfile seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DestinationType destinationType;

    /** ISO-2 country code (e.g. "US") or zone name (e.g. "EUROPE", "ASIA"). */
    @Column(nullable = false, length = 30)
    private String destinationValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShippingMode shippingMode;

    /** Minimum weight this template applies to (inclusive). Null = no minimum. */
    @Column
    private Long minWeightGrams;

    /** Maximum weight this template applies to (inclusive). Null = no maximum. */
    @Column
    private Long maxWeightGrams;

    /** Fixed base cost in INR paise. */
    @Min(0)
    @Column(nullable = false)
    private long baseCostPaise;

    /** Per-kg cost in INR paise. */
    @Min(0)
    @Column(nullable = false)
    private long costPerKgPaise;

    @Column(nullable = false)
    private int estimatedDeliveryDaysMin;

    @Column(nullable = false)
    private int estimatedDeliveryDaysMax;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public SellerShippingTemplate() {}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public enum DestinationType {
        COUNTRY, // e.g. "US", "DE"
        ZONE     // e.g. "ASIA", "EUROPE", "AMERICAS"
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SellerProfile getSeller() { return seller; }
    public void setSeller(SellerProfile seller) { this.seller = seller; }
    public DestinationType getDestinationType() { return destinationType; }
    public void setDestinationType(DestinationType destinationType) { this.destinationType = destinationType; }
    public String getDestinationValue() { return destinationValue; }
    public void setDestinationValue(String destinationValue) { this.destinationValue = destinationValue; }
    public ShippingMode getShippingMode() { return shippingMode; }
    public void setShippingMode(ShippingMode shippingMode) { this.shippingMode = shippingMode; }
    public Long getMinWeightGrams() { return minWeightGrams; }
    public void setMinWeightGrams(Long minWeightGrams) { this.minWeightGrams = minWeightGrams; }
    public Long getMaxWeightGrams() { return maxWeightGrams; }
    public void setMaxWeightGrams(Long maxWeightGrams) { this.maxWeightGrams = maxWeightGrams; }
    public long getBaseCostPaise() { return baseCostPaise; }
    public void setBaseCostPaise(long baseCostPaise) { this.baseCostPaise = baseCostPaise; }
    public long getCostPerKgPaise() { return costPerKgPaise; }
    public void setCostPerKgPaise(long costPerKgPaise) { this.costPerKgPaise = costPerKgPaise; }
    public int getEstimatedDeliveryDaysMin() { return estimatedDeliveryDaysMin; }
    public void setEstimatedDeliveryDaysMin(int estimatedDeliveryDaysMin) { this.estimatedDeliveryDaysMin = estimatedDeliveryDaysMin; }
    public int getEstimatedDeliveryDaysMax() { return estimatedDeliveryDaysMax; }
    public void setEstimatedDeliveryDaysMax(int estimatedDeliveryDaysMax) { this.estimatedDeliveryDaysMax = estimatedDeliveryDaysMax; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
