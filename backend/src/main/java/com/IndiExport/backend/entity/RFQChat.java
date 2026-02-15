package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RFQChat entity for messaging between buyer and seller during RFQ negotiation.
 * Tracks negotiation conversation history.
 */
@Entity
@Table(name = "rfq_chat", indexes = {
        @Index(name = "idx_rfq_chat_rfq_id", columnList = "rfq_id"),
        @Index(name = "idx_rfq_chat_sender_id", columnList = "sender_id"),
        @Index(name = "idx_rfq_chat_sent_at", columnList = "sent_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFQChat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private RFQ rfq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String messageText;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime readAt; // Null until buyer reads seller's message

    public boolean isRead() {
        return readAt != null;
    }
}
