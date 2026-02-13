package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.ShippingMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTOs for cart operations.
 */
public class CartDto {

    public static class CartAddRequest {
        @NotNull(message = "Product ID is required")
        private UUID productId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;

        private ShippingMode shippingMode; // defaults to SEA if null

        public CartAddRequest() {}

        public CartAddRequest(UUID productId, int quantity, ShippingMode shippingMode) {
            this.productId = productId;
            this.quantity = quantity;
            this.shippingMode = shippingMode;
        }

        // Getters and Setters
        public UUID getProductId() { return productId; }
        public void setProductId(UUID productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public ShippingMode getShippingMode() { return shippingMode; }
        public void setShippingMode(ShippingMode shippingMode) { this.shippingMode = shippingMode; }
    }

    public static class CartUpdateRequest {
        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;

        private ShippingMode shippingMode;

        public CartUpdateRequest() {}

        public CartUpdateRequest(int quantity, ShippingMode shippingMode) {
            this.quantity = quantity;
            this.shippingMode = shippingMode;
        }

        // Getters and Setters
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public ShippingMode getShippingMode() { return shippingMode; }
        public void setShippingMode(ShippingMode shippingMode) { this.shippingMode = shippingMode; }
    }

    public static class CartItemResponse {
        private UUID id;
        private UUID productId;
        private String productName;
        private String sku;
        private long unitPricePaise;
        private int quantity;
        private int minQty;
        private long lineTotalPaise;
        private ShippingMode shippingMode;
        private long weightGrams;
        private String sellerCompanyName;
        private UUID sellerId;
        private boolean productActive;
        private LocalDateTime addedAt;

        public CartItemResponse() {}

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getProductId() { return productId; }
        public void setProductId(UUID productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public long getUnitPricePaise() { return unitPricePaise; }
        public void setUnitPricePaise(long unitPricePaise) { this.unitPricePaise = unitPricePaise; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public int getMinQty() { return minQty; }
        public void setMinQty(int minQty) { this.minQty = minQty; }
        public long getLineTotalPaise() { return lineTotalPaise; }
        public void setLineTotalPaise(long lineTotalPaise) { this.lineTotalPaise = lineTotalPaise; }
        public ShippingMode getShippingMode() { return shippingMode; }
        public void setShippingMode(ShippingMode shippingMode) { this.shippingMode = shippingMode; }
        public long getWeightGrams() { return weightGrams; }
        public void setWeightGrams(long weightGrams) { this.weightGrams = weightGrams; }
        public String getSellerCompanyName() { return sellerCompanyName; }
        public void setSellerCompanyName(String sellerCompanyName) { this.sellerCompanyName = sellerCompanyName; }
        public UUID getSellerId() { return sellerId; }
        public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }
        public boolean isProductActive() { return productActive; }
        public void setProductActive(boolean productActive) { this.productActive = productActive; }
        public LocalDateTime getAddedAt() { return addedAt; }
        public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
    }

    public static class CartResponse {
        private List<CartItemResponse> items;
        private int totalItems;
        private long subtotalPaise;
        private long estimatedShippingPaise;
        private long grandTotalPaise;

        public CartResponse() {}

        // Getters and Setters
        public List<CartItemResponse> getItems() { return items; }
        public void setItems(List<CartItemResponse> items) { this.items = items; }
        public int getTotalItems() { return totalItems; }
        public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
        public long getSubtotalPaise() { return subtotalPaise; }
        public void setSubtotalPaise(long subtotalPaise) { this.subtotalPaise = subtotalPaise; }
        public long getEstimatedShippingPaise() { return estimatedShippingPaise; }
        public void setEstimatedShippingPaise(long estimatedShippingPaise) { this.estimatedShippingPaise = estimatedShippingPaise; }
        public long getGrandTotalPaise() { return grandTotalPaise; }
        public void setGrandTotalPaise(long grandTotalPaise) { this.grandTotalPaise = grandTotalPaise; }
    }
}
