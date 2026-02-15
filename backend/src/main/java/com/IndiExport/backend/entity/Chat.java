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
@Table(name = "chats", indexes = {
        @Index(name = "idx_chats_buyer_id", columnList = "buyer_id"),
        @Index(name = "idx_chats_seller_id", columnList = "seller_id"),
        @Index(name = "idx_chats_rfq_id", columnList = "rfq_id"),
        @Index(name = "idx_chats_product_id", columnList = "product_id"),
        @Index(name = "idx_chats_unique_inquiry", columnList = "buyer_id, seller_id, product_id", unique = true),
        @Index(name = "idx_chats_unique_rfq", columnList = "buyer_id, seller_id, rfq_id", unique = true)
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatType chatType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatStatus status = ChatStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private BuyerProfile buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerProfile seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // Null for RFQ_CHAT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id")
    private RFQ rfq; // Null for INQUIRY_CHAT

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
