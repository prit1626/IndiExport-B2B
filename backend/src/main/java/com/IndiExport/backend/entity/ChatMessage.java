package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_messages_chat_id", columnList = "chat_id"),
        @Index(name = "idx_messages_created_at", columnList = "created_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(nullable = false)
    private UUID senderUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role.RoleType senderRole; // BUYER, SELLER, ADMIN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageType messageType;

    @Column(columnDefinition = "TEXT")
    private String messageText;

    @Column(columnDefinition = "TEXT")
    private String fileUrl;

    @Column
    private String fileName;

    @Column(length = 50)
    private String fileMimeType;

    // For PRICE_PROPOSAL
    @Column
    private Long priceInrPaise;
    
    @Column
    private Integer leadTimeDays;
    
    @Column
    private Long shippingEstimateInrPaise;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
