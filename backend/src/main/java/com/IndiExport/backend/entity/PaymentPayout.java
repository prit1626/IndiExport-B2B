package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Records seller payouts via RazorpayX.
 */
@Entity
@Table(name = "payment_payouts", indexes = {
        @Index(name = "idx_pp_payment_id", columnList = "payment_id"),
        @Index(name = "idx_pp_seller_id", columnList = "seller_id"),
        @Index(name = "idx_pp_status", columnList = "status"),
        @Index(name = "idx_pp_provider_payout_id", columnList = "provider_payout_id")
})
public class PaymentPayout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentProvider provider = PaymentProvider.RAZORPAYX;

    @Column(name = "provider_payout_id", length = 200)
    private String providerPayoutId;

    @Column(nullable = false)
    private long amountInrPaise;

    @Column(nullable = false)
    private long commissionPaise = 0;

    @Column(nullable = false)
    private long exchangeRateMicros;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayoutStatus status = PayoutStatus.CREATED;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column
    private Instant completedAt;

    public static PaymentPayoutBuilder builder() {
        return new PaymentPayoutBuilder();
    }

    public static class PaymentPayoutBuilder {
        private PaymentPayout payout = new PaymentPayout();
        public PaymentPayoutBuilder payment(Payment payment) { payout.setPayment(payment); return this; }
        public PaymentPayoutBuilder sellerId(UUID sellerId) { payout.setSellerId(sellerId); return this; }
        public PaymentPayoutBuilder provider(PaymentProvider provider) { payout.setProvider(provider); return this; }
        public PaymentPayoutBuilder amountInrPaise(long amount) { payout.setAmountInrPaise(amount); return this; }
        public PaymentPayoutBuilder commissionPaise(long commission) { payout.setCommissionPaise(commission); return this; }
        public PaymentPayoutBuilder exchangeRateMicros(long rate) { payout.setExchangeRateMicros(rate); return this; }
        public PaymentPayoutBuilder status(PayoutStatus status) { payout.setStatus(status); return this; }
        public PaymentPayout build() { return payout; }
    }

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public UUID getSellerId() { return sellerId; }
    public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }
    public PaymentProvider getProvider() { return provider; }
    public void setProvider(PaymentProvider provider) { this.provider = provider; }
    public String getProviderPayoutId() { return providerPayoutId; }
    public void setProviderPayoutId(String providerPayoutId) { this.providerPayoutId = providerPayoutId; }
    public long getAmountInrPaise() { return amountInrPaise; }
    public void setAmountInrPaise(long amountInrPaise) { this.amountInrPaise = amountInrPaise; }
    public long getCommissionPaise() { return commissionPaise; }
    public void setCommissionPaise(long commissionPaise) { this.commissionPaise = commissionPaise; }
    public long getExchangeRateMicros() { return exchangeRateMicros; }
    public void setExchangeRateMicros(long exchangeRateMicros) { this.exchangeRateMicros = exchangeRateMicros; }
    public PayoutStatus getStatus() { return status; }
    public void setStatus(PayoutStatus status) { this.status = status; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
