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

    /* ── Buyer: Create Payment ── */

    public static class CreatePaymentResponse {
        private UUID paymentId;
        private PaymentProvider provider;
        private String providerPaymentIntentId;
        private String clientSecret;
        private long amountMinor;
        private String currency;
        private PaymentStatus status;

        public CreatePaymentResponse() {}

        // Getters and Setters
        public UUID getPaymentId() { return paymentId; }
        public void setPaymentId(UUID paymentId) { this.paymentId = paymentId; }
        public PaymentProvider getProvider() { return provider; }
        public void setProvider(PaymentProvider provider) { this.provider = provider; }
        public String getProviderPaymentIntentId() { return providerPaymentIntentId; }
        public void setProviderPaymentIntentId(String providerPaymentIntentId) { this.providerPaymentIntentId = providerPaymentIntentId; }
        public String getClientSecret() { return clientSecret; }
        public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
        public long getAmountMinor() { return amountMinor; }
        public void setAmountMinor(long amountMinor) { this.amountMinor = amountMinor; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public PaymentStatus getStatus() { return status; }
        public void setStatus(PaymentStatus status) { this.status = status; }
    }

    /* ── Payment Status ── */

    public static class PaymentStatusResponse {
        private UUID paymentId;
        private UUID orderId;
        private PaymentProvider provider;
        private PaymentStatus status;
        private long amountMinor;
        private String currency;
        private long amountInrPaise;
        private boolean disputeLocked;
        private Instant createdAt;
        private Instant capturedAt;
        private Instant releasedAt;

        public PaymentStatusResponse() {}

        // Getters and Setters
        public UUID getPaymentId() { return paymentId; }
        public void setPaymentId(UUID paymentId) { this.paymentId = paymentId; }
        public UUID getOrderId() { return orderId; }
        public void setOrderId(UUID orderId) { this.orderId = orderId; }
        public PaymentProvider getProvider() { return provider; }
        public void setProvider(PaymentProvider provider) { this.provider = provider; }
        public PaymentStatus getStatus() { return status; }
        public void setStatus(PaymentStatus status) { this.status = status; }
        public long getAmountMinor() { return amountMinor; }
        public void setAmountMinor(long amountMinor) { this.amountMinor = amountMinor; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public long getAmountInrPaise() { return amountInrPaise; }
        public void setAmountInrPaise(long amountInrPaise) { this.amountInrPaise = amountInrPaise; }
        public boolean isDisputeLocked() { return disputeLocked; }
        public void setDisputeLocked(boolean disputeLocked) { this.disputeLocked = disputeLocked; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public Instant getCapturedAt() { return capturedAt; }
        public void setCapturedAt(Instant capturedAt) { this.capturedAt = capturedAt; }
        public Instant getReleasedAt() { return releasedAt; }
        public void setReleasedAt(Instant releasedAt) { this.releasedAt = releasedAt; }
    }

    /* ── Admin: Full Payment Details ── */

    public static class AdminPaymentResponse {
        private UUID paymentId;
        private UUID orderId;
        private String orderNumber;
        private UUID buyerId;
        private UUID sellerId;
        private PaymentProvider provider;
        private String providerPaymentIntentId;
        private PaymentStatus status;
        private long amountMinor;
        private String currency;
        private long amountInrPaise;
        private boolean disputeLocked;
        private Instant createdAt;
        private Instant capturedAt;
        private Instant holdingStartedAt;
        private Instant releasedAt;
        private Instant refundedAt;
        private PayoutSummary payout;

        public AdminPaymentResponse() {}

        // Getters and Setters
        public UUID getPaymentId() { return paymentId; }
        public void setPaymentId(UUID paymentId) { this.paymentId = paymentId; }
        public UUID getOrderId() { return orderId; }
        public void setOrderId(UUID orderId) { this.orderId = orderId; }
        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
        public UUID getBuyerId() { return buyerId; }
        public void setBuyerId(UUID buyerId) { this.buyerId = buyerId; }
        public UUID getSellerId() { return sellerId; }
        public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }
        public PaymentProvider getProvider() { return provider; }
        public void setProvider(PaymentProvider provider) { this.provider = provider; }
        public String getProviderPaymentIntentId() { return providerPaymentIntentId; }
        public void setProviderPaymentIntentId(String providerPaymentIntentId) { this.providerPaymentIntentId = providerPaymentIntentId; }
        public PaymentStatus getStatus() { return status; }
        public void setStatus(PaymentStatus status) { this.status = status; }
        public long getAmountMinor() { return amountMinor; }
        public void setAmountMinor(long amountMinor) { this.amountMinor = amountMinor; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public long getAmountInrPaise() { return amountInrPaise; }
        public void setAmountInrPaise(long amountInrPaise) { this.amountInrPaise = amountInrPaise; }
        public boolean isDisputeLocked() { return disputeLocked; }
        public void setDisputeLocked(boolean disputeLocked) { this.disputeLocked = disputeLocked; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public Instant getCapturedAt() { return capturedAt; }
        public void setCapturedAt(Instant capturedAt) { this.capturedAt = capturedAt; }
        public Instant getHoldingStartedAt() { return holdingStartedAt; }
        public void setHoldingStartedAt(Instant holdingStartedAt) { this.holdingStartedAt = holdingStartedAt; }
        public Instant getReleasedAt() { return releasedAt; }
        public void setReleasedAt(Instant releasedAt) { this.releasedAt = releasedAt; }
        public Instant getRefundedAt() { return refundedAt; }
        public void setRefundedAt(Instant refundedAt) { this.refundedAt = refundedAt; }
        public PayoutSummary getPayout() { return payout; }
        public void setPayout(PayoutSummary payout) { this.payout = payout; }
    }

    public static class PayoutSummary {
        private UUID payoutId;
        private long amountInrPaise;
        private long commissionPaise;
        private PayoutStatus status;
        private String providerPayoutId;
        private Instant completedAt;

        public PayoutSummary() {}

        // Getters and Setters
        public UUID getPayoutId() { return payoutId; }
        public void setPayoutId(UUID payoutId) { this.payoutId = payoutId; }
        public long getAmountInrPaise() { return amountInrPaise; }
        public void setAmountInrPaise(long amountInrPaise) { this.amountInrPaise = amountInrPaise; }
        public long getCommissionPaise() { return commissionPaise; }
        public void setCommissionPaise(long commissionPaise) { this.commissionPaise = commissionPaise; }
        public PayoutStatus getStatus() { return status; }
        public void setStatus(PayoutStatus status) { this.status = status; }
        public String getProviderPayoutId() { return providerPayoutId; }
        public void setProviderPayoutId(String providerPayoutId) { this.providerPayoutId = providerPayoutId; }
        public Instant getCompletedAt() { return completedAt; }
        public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    }

    /* ── Payout Response ── */

    public static class PayoutResponse {
        private UUID payoutId;
        private UUID paymentId;
        private UUID sellerId;
        private long amountInrPaise;
        private long commissionPaise;
        private long exchangeRateMicros;
        private PayoutStatus status;
        private String providerPayoutId;
        private Instant createdAt;
        private Instant completedAt;

        public PayoutResponse() {}

        public static PayoutResponseBuilder builder() {
            return new PayoutResponseBuilder();
        }

        public static class PayoutResponseBuilder {
            private PayoutResponse response = new PayoutResponse();
            public PayoutResponseBuilder payoutId(UUID id) { response.setPayoutId(id); return this; }
            public PayoutResponseBuilder paymentId(UUID id) { response.setPaymentId(id); return this; }
            public PayoutResponseBuilder sellerId(UUID id) { response.setSellerId(id); return this; }
            public PayoutResponseBuilder amountInrPaise(long amount) { response.setAmountInrPaise(amount); return this; }
            public PayoutResponseBuilder commissionPaise(long commission) { response.setCommissionPaise(commission); return this; }
            public PayoutResponseBuilder exchangeRateMicros(long rate) { response.setExchangeRateMicros(rate); return this; }
            public PayoutResponseBuilder status(PayoutStatus status) { response.setStatus(status); return this; }
            public PayoutResponseBuilder providerPayoutId(String id) { response.setProviderPayoutId(id); return this; }
            public PayoutResponseBuilder createdAt(Instant at) { response.setCreatedAt(at); return this; }
            public PayoutResponseBuilder completedAt(Instant at) { response.setCompletedAt(at); return this; }
            public PayoutResponse build() { return response; }
        }

        // Getters and Setters
        public UUID getPayoutId() { return payoutId; }
        public void setPayoutId(UUID payoutId) { this.payoutId = payoutId; }
        public UUID getPaymentId() { return paymentId; }
        public void setPaymentId(UUID paymentId) { this.paymentId = paymentId; }
        public UUID getSellerId() { return sellerId; }
        public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }
        public long getAmountInrPaise() { return amountInrPaise; }
        public void setAmountInrPaise(long amountInrPaise) { this.amountInrPaise = amountInrPaise; }
        public long getCommissionPaise() { return commissionPaise; }
        public void setCommissionPaise(long commissionPaise) { this.commissionPaise = commissionPaise; }
        public long getExchangeRateMicros() { return exchangeRateMicros; }
        public void setExchangeRateMicros(long exchangeRateMicros) { this.exchangeRateMicros = exchangeRateMicros; }
        public PayoutStatus getStatus() { return status; }
        public void setStatus(PayoutStatus status) { this.status = status; }
        public String getProviderPayoutId() { return providerPayoutId; }
        public void setProviderPayoutId(String providerPayoutId) { this.providerPayoutId = providerPayoutId; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public Instant getCompletedAt() { return completedAt; }
        public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    }

    /* ── Webhook ── */

    public static class WebhookResponse {
        private boolean received;
        private String message;

        public WebhookResponse() {}

        public WebhookResponse(boolean received, String message) {
            this.received = received;
            this.message = message;
        }

        public static WebhookResponseBuilder builder() {
            return new WebhookResponseBuilder();
        }

        public static class WebhookResponseBuilder {
            private WebhookResponse response = new WebhookResponse();
            public WebhookResponseBuilder received(boolean received) { response.setReceived(received); return this; }
            public WebhookResponseBuilder message(String message) { response.setMessage(message); return this; }
            public WebhookResponse build() { return response; }
        }

        // Getters and Setters
        public boolean isReceived() { return received; }
        public void setReceived(boolean received) { this.received = received; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class WebhookProcessed {
        private boolean processed;
        private String eventId;

        public WebhookProcessed() {}

        public WebhookProcessed(boolean processed, String eventId) {
            this.processed = processed;
            this.eventId = eventId;
        }

        // Getters and Setters
        public boolean isProcessed() { return processed; }
        public void setProcessed(boolean processed) { this.processed = processed; }
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
    }
}
