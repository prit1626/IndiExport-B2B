package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Payment entity — escrow-aware payment tracking.
 * All monetary values stored as long in minor units.
 */
@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_order_id", columnList = "order_id"),
        @Index(name = "idx_payments_buyer_id", columnList = "buyer_id"),
        @Index(name = "idx_payments_seller_id", columnList = "seller_id"),
        @Index(name = "idx_payments_status", columnList = "status"),
        @Index(name = "idx_payments_provider_pi_id", columnList = "provider_payment_intent_id"),
        @Index(name = "idx_payments_created_at", columnList = "created_at")
})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "buyer_id", nullable = false)
    private UUID buyerId;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    /* ── Provider fields ── */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentProvider provider;

    @Column(name = "provider_payment_intent_id", length = 200)
    private String providerPaymentIntentId;

    @Column(length = 500)
    private String providerClientSecret;

    /* ── Money ── */

    @Column(nullable = false)
    private long amountMinor;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private long amountInrPaise;

    /* ── Status ── */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.CREATED;

    @Column(nullable = false)
    private boolean disputeLocked = false;

    /* ── Timestamps ── */

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @Column
    private Instant capturedAt;

    @Column
    private Instant holdingStartedAt;

    @Column
    private Instant releasedAt;

    @Column
    private Instant refundedAt;

    @Column(columnDefinition = "TEXT")
    private String lastWebhookPayload;

    public Payment() {}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public UUID getBuyerId() { return buyerId; }
    public void setBuyerId(UUID buyerId) { this.buyerId = buyerId; }
    public UUID getSellerId() { return sellerId; }
    public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }
    public PaymentProvider getProvider() { return provider; }
    public void setProvider(PaymentProvider provider) { this.provider = provider; }
    public String getProviderPaymentIntentId() { return providerPaymentIntentId; }
    public void setProviderPaymentIntentId(String providerPaymentIntentId) { this.providerPaymentIntentId = providerPaymentIntentId; }
    public String getProviderClientSecret() { return providerClientSecret; }
    public void setProviderClientSecret(String providerClientSecret) { this.providerClientSecret = providerClientSecret; }
    public long getAmountMinor() { return amountMinor; }
    public void setAmountMinor(long amountMinor) { this.amountMinor = amountMinor; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public long getAmountInrPaise() { return amountInrPaise; }
    public void setAmountInrPaise(long amountInrPaise) { this.amountInrPaise = amountInrPaise; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public boolean isDisputeLocked() { return disputeLocked; }
    public void setDisputeLocked(boolean disputeLocked) { this.disputeLocked = disputeLocked; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Instant getCapturedAt() { return capturedAt; }
    public void setCapturedAt(Instant capturedAt) { this.capturedAt = capturedAt; }
    public Instant getHoldingStartedAt() { return holdingStartedAt; }
    public void setHoldingStartedAt(Instant holdingStartedAt) { this.holdingStartedAt = holdingStartedAt; }
    public Instant getReleasedAt() { return releasedAt; }
    public void setReleasedAt(Instant releasedAt) { this.releasedAt = releasedAt; }
    public Instant getRefundedAt() { return refundedAt; }
    public void setRefundedAt(Instant refundedAt) { this.refundedAt = refundedAt; }
    public String getLastWebhookPayload() { return lastWebhookPayload; }
    public void setLastWebhookPayload(String lastWebhookPayload) { this.lastWebhookPayload = lastWebhookPayload; }
}
