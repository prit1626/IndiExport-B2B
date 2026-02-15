package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import com.IndiExport.backend.entity.Role.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "disputes", indexes = {
        @Index(name = "idx_disputes_order_id", columnList = "order_id", unique = true),
        @Index(name = "idx_disputes_buyer_id", columnList = "buyer_id"),
        @Index(name = "idx_disputes_seller_id", columnList = "seller_id"),
        @Index(name = "idx_disputes_status", columnList = "status"),
        @Index(name = "idx_disputes_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "buyer_id", nullable = false)
    private UUID buyerId;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Column(name = "raised_by_user_id", nullable = false)
    private UUID raisedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleType raisedByRole; // BUYER or SELLER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DisputeReason reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DisputeStatus status = DisputeStatus.OPEN;

    /* ── Resolution ── */

    @Column
    private Instant resolvedAt;

    @Column
    private UUID resolvedByAdminId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DisputeResolutionAction resolutionAction;

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column
    private Long partialRefundAmountMinor; // Only if PARTIAL_REFUND

    /* ── Timestamps ── */

    @Column(nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    /* ── Relationships ── */

    @OneToMany(mappedBy = "dispute", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DisputeEvidence> evidence = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public boolean isUnderDispute() {
        return status != DisputeStatus.RESOLVED && status != DisputeStatus.REJECTED;
    }
}
