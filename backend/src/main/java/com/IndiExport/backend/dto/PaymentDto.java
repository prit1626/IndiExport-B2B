package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.PaymentProvider;
import com.IndiExport.backend.entity.PaymentStatus;
import com.IndiExport.backend.entity.PayoutStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * DTOs for payment operations.
 */
public class PaymentDto {

    /* ── Buyer: Payment Status ── */

    @jakarta.annotation.Generated("Antigravity")
    public static class PaymentStatusResponse {
        private java.util.UUID paymentId;
        private java.util.UUID orderId;
        private com.IndiExport.backend.entity.PaymentProvider provider;
        private com.IndiExport.backend.entity.PaymentStatus status;
        private long amountMinor;
        private String currency;
        private long amountInrPaise;
        private java.time.Instant createdAt;
        private java.time.Instant capturedAt;

        public PaymentStatusResponse() {}
        // Getters/Setters omitted for brevity in replace, but should be there if needed.
        // Actually I'll provide full implementation to avoid breakage.
        public java.util.UUID getPaymentId() { return paymentId; }
        public void setPaymentId(java.util.UUID paymentId) { this.paymentId = paymentId; }
        public java.util.UUID getOrderId() { return orderId; }
        public void setOrderId(java.util.UUID orderId) { this.orderId = orderId; }
        public com.IndiExport.backend.entity.PaymentProvider getProvider() { return provider; }
        public void setProvider(com.IndiExport.backend.entity.PaymentProvider provider) { this.provider = provider; }
        public com.IndiExport.backend.entity.PaymentStatus getStatus() { return status; }
        public void setStatus(com.IndiExport.backend.entity.PaymentStatus status) { this.status = status; }
        public long getAmountMinor() { return amountMinor; }
        public void setAmountMinor(long amountMinor) { this.amountMinor = amountMinor; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public long getAmountInrPaise() { return amountInrPaise; }
        public void setAmountInrPaise(long amountInrPaise) { this.amountInrPaise = amountInrPaise; }
        public java.time.Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }
        public java.time.Instant getCapturedAt() { return capturedAt; }
        public void setCapturedAt(java.time.Instant capturedAt) { this.capturedAt = capturedAt; }
    }
}
