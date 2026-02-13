package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Dispute entity for handling order disputes between buyer and seller.
 * Tracks dispute status, resolution, and whether seller's payouts are frozen.
 * Payout frozen if payout_frozen = TRUE (prevents seller receiving payments during active dispute).
 */
@Entity
@Table(name = "disputes", indexes = {
        @Index(name = "idx_disputes_order_id", columnList = "order_id"),
        @Index(name = "idx_disputes_status", columnList = "status"),
        @Index(name = "idx_disputes_payout_frozen", columnList = "payout_frozen"),
        @Index(name = "idx_disputes_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by_id", nullable = false)
    private User raisedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DisputeStatus status = DisputeStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DisputeReason reason = DisputeReason.PENDING;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean payoutFrozen = true; // Seller cannot receive payments if TRUE

    @Column(columnDefinition = "TEXT")
    private String adminResolutionNotes;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ResolutionOutcome resolutionOutcome; // 'BUYER_FAVOR', 'SELLER_FAVOR', 'PARTIAL', 'REJECTED'

    @Column(precision = 15, scale = 2)
    private java.math.BigDecimal refundAmount; // Amount refunded if dispute resolved

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_id")
    private User resolvedBy;

    public enum DisputeStatus {
        OPEN,       // Dispute raised, awaiting admin review
        INVESTIGATING,
        RESOLVED,   // Dispute closed
        ESCALATED   // Escalated to higher authority
    }

    public enum DisputeReason {
        PENDING,                   // Placeholder
        QUALITY_MISMATCH,         // Product quality doesn't match description
        PARTIAL_DELIVERY,         // Received partial items
        DAMAGED_GOODS,            // Items arrived damaged
        WRONG_ITEM_SHIPPED,       // Wrong product shipped
        DELIVERY_DELAY,           // Delivery delayed
        NON_DELIVERY,             // Items not delivered
        PAYMENT_ISSUE             // Payment-related issues
    }

    public enum ResolutionOutcome {
        BUYER_FAVOR,   // Refund issued to buyer
        SELLER_FAVOR,  // Dispute rejected, seller keeps payment
        PARTIAL,       // Partial refund
        REJECTED       // Dispute doesn't fall within scope
    }

    @PreUpdate
    protected void onUpdate() {
        if (status == DisputeStatus.RESOLVED && resolutionOutcome != null) {
            payoutFrozen = false; // Unfreeze payout once resolved
        }
    }

    public boolean isUnderDispute() {
        return status != DisputeStatus.RESOLVED && payoutFrozen;
    }
}
