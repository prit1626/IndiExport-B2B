package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.ShippingMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTOs for the public shipping quote API.
 */
public class ShippingQuoteDto {

    public static class QuoteRequest {
        @NotBlank(message = "Destination country code is required")
        private String destinationCountryCode; // ISO-2

        @NotNull(message = "Shipping mode is required")
        private ShippingMode shippingMode;

        @NotEmpty(message = "At least one item is required")
        private List<QuoteItem> items;

        /** Optional: if provided, try seller-specific templates first. */
        private UUID sellerId;

        public QuoteRequest() {}

        // Getters and Setters
        public String getDestinationCountryCode() { return destinationCountryCode; }
        public void setDestinationCountryCode(String destinationCountryCode) { this.destinationCountryCode = destinationCountryCode; }
        public ShippingMode getShippingMode() { return shippingMode; }
        public void setShippingMode(ShippingMode shippingMode) { this.shippingMode = shippingMode; }
        public List<QuoteItem> getItems() { return items; }
        public void setItems(List<QuoteItem> items) { this.items = items; }
        public UUID getSellerId() { return sellerId; }
        public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }
    }

    public static class QuoteItem {
        @NotNull(message = "Product ID is required")
        private UUID productId;

        private int quantity;

        public QuoteItem() {}

        // Getters and Setters
        public UUID getProductId() { return productId; }
        public void setProductId(UUID productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class QuoteResponse {
        private long shippingCostPaise;
        private long totalWeightGrams;
        private long chargeableWeightGrams;
        private int estimatedDeliveryDaysMin;
        private int estimatedDeliveryDaysMax;
        private String quoteSource; // SELLER_TEMPLATE or GLOBAL_CALCULATOR
        private QuoteBreakdown breakdown;
        private Instant quotedAt;

        public QuoteResponse() {}

        // Getters and Setters
        public long getShippingCostPaise() { return shippingCostPaise; }
        public void setShippingCostPaise(long shippingCostPaise) { this.shippingCostPaise = shippingCostPaise; }
        public long getTotalWeightGrams() { return totalWeightGrams; }
        public void setTotalWeightGrams(long totalWeightGrams) { this.totalWeightGrams = totalWeightGrams; }
        public long getChargeableWeightGrams() { return chargeableWeightGrams; }
        public void setChargeableWeightGrams(long chargeableWeightGrams) { this.chargeableWeightGrams = chargeableWeightGrams; }
        public int getEstimatedDeliveryDaysMin() { return estimatedDeliveryDaysMin; }
        public void setEstimatedDeliveryDaysMin(int estimatedDeliveryDaysMin) { this.estimatedDeliveryDaysMin = estimatedDeliveryDaysMin; }
        public int getEstimatedDeliveryDaysMax() { return estimatedDeliveryDaysMax; }
        public void setEstimatedDeliveryDaysMax(int estimatedDeliveryDaysMax) { this.estimatedDeliveryDaysMax = estimatedDeliveryDaysMax; }
        public String getQuoteSource() { return quoteSource; }
        public void setQuoteSource(String quoteSource) { this.quoteSource = quoteSource; }
        public QuoteBreakdown getBreakdown() { return breakdown; }
        public void setBreakdown(QuoteBreakdown breakdown) { this.breakdown = breakdown; }
        public Instant getQuotedAt() { return quotedAt; }
        public void setQuotedAt(Instant quotedAt) { this.quotedAt = quotedAt; }
    }

    public static class QuoteBreakdown {
        private long baseCostPaise;
        private long weightChargePaise;
        private long chargeableWeightKg;
        private String shippingZone;
        private double zoneMultiplier;

        public QuoteBreakdown() {}

        // Getters and Setters
        public long getBaseCostPaise() { return baseCostPaise; }
        public void setBaseCostPaise(long baseCostPaise) { this.baseCostPaise = baseCostPaise; }
        public long getWeightChargePaise() { return weightChargePaise; }
        public void setWeightChargePaise(long weightChargePaise) { this.weightChargePaise = weightChargePaise; }
        public long getChargeableWeightKg() { return chargeableWeightKg; }
        public void setChargeableWeightKg(long chargeableWeightKg) { this.chargeableWeightKg = chargeableWeightKg; }
        public String getShippingZone() { return shippingZone; }
        public void setShippingZone(String shippingZone) { this.shippingZone = shippingZone; }
        public double getZoneMultiplier() { return zoneMultiplier; }
        public void setZoneMultiplier(double zoneMultiplier) { this.zoneMultiplier = zoneMultiplier; }
    }
}
