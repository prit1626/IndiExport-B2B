package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.SellerShippingTemplate;
import com.IndiExport.backend.entity.ShippingMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

/**
 * DTOs for seller shipping templates.
 */
public class ShippingTemplateDto {

    public static class CreateRequest {
        @NotNull(message = "Destination type is required")
        private SellerShippingTemplate.DestinationType destinationType;

        @NotBlank(message = "Destination value is required")
        private String destinationValue;

        @NotNull(message = "Shipping mode is required")
        private ShippingMode shippingMode;

        private Long minWeightGrams;
        private Long maxWeightGrams;

        @Min(value = 0, message = "Base cost must be >= 0")
        private long baseCostPaise;

        @Min(value = 0, message = "Cost per kg must be >= 0")
        private long costPerKgPaise;

        @Min(value = 1, message = "Min delivery days must be >= 1")
        private int estimatedDeliveryDaysMin;

        @Min(value = 1, message = "Max delivery days must be >= 1")
        private int estimatedDeliveryDaysMax;

        public CreateRequest() {}

        // Getters and Setters
        public SellerShippingTemplate.DestinationType getDestinationType() { return destinationType; }
        public void setDestinationType(SellerShippingTemplate.DestinationType destinationType) { this.destinationType = destinationType; }
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
    }

    public static class UpdateRequest {
        private SellerShippingTemplate.DestinationType destinationType;
        private String destinationValue;
        private ShippingMode shippingMode;
        private Long minWeightGrams;
        private Long maxWeightGrams;
        private Long baseCostPaise;
        private Long costPerKgPaise;
        private Integer estimatedDeliveryDaysMin;
        private Integer estimatedDeliveryDaysMax;
        private Boolean active;

        public UpdateRequest() {}

        // Getters and Setters
        public SellerShippingTemplate.DestinationType getDestinationType() { return destinationType; }
        public void setDestinationType(SellerShippingTemplate.DestinationType destinationType) { this.destinationType = destinationType; }
        public String getDestinationValue() { return destinationValue; }
        public void setDestinationValue(String destinationValue) { this.destinationValue = destinationValue; }
        public ShippingMode getShippingMode() { return shippingMode; }
        public void setShippingMode(ShippingMode shippingMode) { this.shippingMode = shippingMode; }
        public Long getMinWeightGrams() { return minWeightGrams; }
        public void setMinWeightGrams(Long minWeightGrams) { this.minWeightGrams = minWeightGrams; }
        public Long getMaxWeightGrams() { return maxWeightGrams; }
        public void setMaxWeightGrams(Long maxWeightGrams) { this.maxWeightGrams = maxWeightGrams; }
        public Long getBaseCostPaise() { return baseCostPaise; }
        public void setBaseCostPaise(Long baseCostPaise) { this.baseCostPaise = baseCostPaise; }
        public Long getCostPerKgPaise() { return costPerKgPaise; }
        public void setCostPerKgPaise(Long costPerKgPaise) { this.costPerKgPaise = costPerKgPaise; }
        public Integer getEstimatedDeliveryDaysMin() { return estimatedDeliveryDaysMin; }
        public void setEstimatedDeliveryDaysMin(Integer estimatedDeliveryDaysMin) { this.estimatedDeliveryDaysMin = estimatedDeliveryDaysMin; }
        public Integer getEstimatedDeliveryDaysMax() { return estimatedDeliveryDaysMax; }
        public void setEstimatedDeliveryDaysMax(Integer estimatedDeliveryDaysMax) { this.estimatedDeliveryDaysMax = estimatedDeliveryDaysMax; }
        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
    }

    public static class Response {
        private UUID id;
        private UUID sellerId;
        private SellerShippingTemplate.DestinationType destinationType;
        private String destinationValue;
        private ShippingMode shippingMode;
        private Long minWeightGrams;
        private Long maxWeightGrams;
        private long baseCostPaise;
        private long costPerKgPaise;
        private int estimatedDeliveryDaysMin;
        private int estimatedDeliveryDaysMax;
        private boolean active;
        private Instant createdAt;
        private Instant updatedAt;

        public Response() {}

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getSellerId() { return sellerId; }
        public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }
        public SellerShippingTemplate.DestinationType getDestinationType() { return destinationType; }
        public void setDestinationType(SellerShippingTemplate.DestinationType destinationType) { this.destinationType = destinationType; }
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
}
