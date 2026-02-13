package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.ShippingMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTOs for checkout flow.
 */
public class CheckoutDto {

    public static class CheckoutRequest {
        @NotNull(message = "Shipping address is required")
        private AddressInfo shippingAddress;

        @NotNull(message = "Shipping mode is required")
        private ShippingMode shippingMode;

        @NotBlank(message = "Buyer currency is required")
        private String buyerCurrency;

        private String specialInstructions;

        public CheckoutRequest() {}

        // Getters and Setters
        public AddressInfo getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(AddressInfo shippingAddress) { this.shippingAddress = shippingAddress; }
        public ShippingMode getShippingMode() { return shippingMode; }
        public void setShippingMode(ShippingMode shippingMode) { this.shippingMode = shippingMode; }
        public String getBuyerCurrency() { return buyerCurrency; }
        public void setBuyerCurrency(String buyerCurrency) { this.buyerCurrency = buyerCurrency; }
        public String getSpecialInstructions() { return specialInstructions; }
        public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
    }

    public static class AddressInfo {
        @NotBlank(message = "Address is required")
        private String address;

        @NotBlank(message = "City is required")
        private String city;

        private String state;

        private String postalCode;

        @NotBlank(message = "Country is required")
        private String country; // ISO-2

        public AddressInfo() {}

        // Getters and Setters
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }

    public static class CheckoutResponse {
        private List<OrderSummary> orders;
        private long totalSubtotalPaise;
        private long totalShippingPaise;
        private long grandTotalPaise;
        private long grandTotalConverted;
        private String buyerCurrency;
        private long exchangeRateMicros;
        private Instant rateTimestamp;
        private boolean paymentRequired;

        public CheckoutResponse() {}

        // Getters and Setters
        public List<OrderSummary> getOrders() { return orders; }
        public void setOrders(List<OrderSummary> orders) { this.orders = orders; }
        public long getTotalSubtotalPaise() { return totalSubtotalPaise; }
        public void setTotalSubtotalPaise(long totalSubtotalPaise) { this.totalSubtotalPaise = totalSubtotalPaise; }
        public long getTotalShippingPaise() { return totalShippingPaise; }
        public void setTotalShippingPaise(long totalShippingPaise) { this.totalShippingPaise = totalShippingPaise; }
        public long getGrandTotalPaise() { return grandTotalPaise; }
        public void setGrandTotalPaise(long grandTotalPaise) { this.grandTotalPaise = grandTotalPaise; }
        public long getGrandTotalConverted() { return grandTotalConverted; }
        public void setGrandTotalConverted(long grandTotalConverted) { this.grandTotalConverted = grandTotalConverted; }
        public String getBuyerCurrency() { return buyerCurrency; }
        public void setBuyerCurrency(String buyerCurrency) { this.buyerCurrency = buyerCurrency; }
        public long getExchangeRateMicros() { return exchangeRateMicros; }
        public void setExchangeRateMicros(long exchangeRateMicros) { this.exchangeRateMicros = exchangeRateMicros; }
        public Instant getRateTimestamp() { return rateTimestamp; }
        public void setRateTimestamp(Instant rateTimestamp) { this.rateTimestamp = rateTimestamp; }
        public boolean isPaymentRequired() { return paymentRequired; }
        public void setPaymentRequired(boolean paymentRequired) { this.paymentRequired = paymentRequired; }
    }

    public static class OrderSummary {
        private UUID orderId;
        private String orderNumber;
        private String sellerCompanyName;
        private UUID sellerId;
        private int itemCount;
        private long subtotalPaise;
        private long shippingCostPaise;
        private long totalPaise;
        private long totalConverted;
        private String buyerCurrency;
        private ShippingMode shippingMode;
        private int estimatedDeliveryDaysMin;
        private int estimatedDeliveryDaysMax;

        public OrderSummary() {}

        // Getters and Setters
        public UUID getOrderId() { return orderId; }
        public void setOrderId(UUID orderId) { this.orderId = orderId; }
        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
        public String getSellerCompanyName() { return sellerCompanyName; }
        public void setSellerCompanyName(String sellerCompanyName) { this.sellerCompanyName = sellerCompanyName; }
        public UUID getSellerId() { return sellerId; }
        public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }
        public int getItemCount() { return itemCount; }
        public void setItemCount(int itemCount) { this.itemCount = itemCount; }
        public long getSubtotalPaise() { return subtotalPaise; }
        public void setSubtotalPaise(long subtotalPaise) { this.subtotalPaise = subtotalPaise; }
        public long getShippingCostPaise() { return shippingCostPaise; }
        public void setShippingCostPaise(long shippingCostPaise) { this.shippingCostPaise = shippingCostPaise; }
        public long getTotalPaise() { return totalPaise; }
        public void setTotalPaise(long totalPaise) { this.totalPaise = totalPaise; }
        public long getTotalConverted() { return totalConverted; }
        public void setTotalConverted(long totalConverted) { this.totalConverted = totalConverted; }
        public String getBuyerCurrency() { return buyerCurrency; }
        public void setBuyerCurrency(String buyerCurrency) { this.buyerCurrency = buyerCurrency; }
        public ShippingMode getShippingMode() { return shippingMode; }
        public void setShippingMode(ShippingMode shippingMode) { this.shippingMode = shippingMode; }
        public int getEstimatedDeliveryDaysMin() { return estimatedDeliveryDaysMin; }
        public void setEstimatedDeliveryDaysMin(int estimatedDeliveryDaysMin) { this.estimatedDeliveryDaysMin = estimatedDeliveryDaysMin; }
        public int getEstimatedDeliveryDaysMax() { return estimatedDeliveryDaysMax; }
        public void setEstimatedDeliveryDaysMax(int estimatedDeliveryDaysMax) { this.estimatedDeliveryDaysMax = estimatedDeliveryDaysMax; }
    }
}
