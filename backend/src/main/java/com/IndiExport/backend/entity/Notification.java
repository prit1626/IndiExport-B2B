package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Notification entity for system notifications to users.
 * Tracks notification type, status (read/unread), and content.
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notifications_user_id", columnList = "user_id"),
        @Index(name = "idx_notifications_is_read", columnList = "is_read"),
        @Index(name = "idx_notifications_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type; // ORDER_CONFIRMED, PAYMENT_RECEIVED, etc.

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean isRead = false;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime readAt;

    @Column
    private UUID relatedEntityId; // ID of related order, product, etc.

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime dismissedAt;

    public enum NotificationType {
        ORDER_CREATED,
        ORDER_CONFIRMED,
        ORDER_SHIPPED,
        ORDER_DELIVERED,
        PAYMENT_RECEIVED,
        PAYMENT_FAILED,
        PRODUCT_ACTIVATED,
        PRODUCT_DEACTIVATED,
        DISPUTE_OPENED,
        DISPUTE_RESOLVED,
        RFQ_RECEIVED,
        RFQ_ACCEPTED,
        RFQ_REJECTED,
        REVIEW_RECEIVED,
        IEC_VERIFICATION_UPDATE
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}
