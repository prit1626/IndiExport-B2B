package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a negotiation chat channel between a buyer and a specific seller for one RFQ.
 * Unique per (rfq_id, seller_id).
 * isActive = false means the chat is locked (RFQ finalized / cancelled / expired).
 */
@Entity
@Table(
    name = "rfq_chats",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_rfq_chat_rfq_seller",
        columnNames = {"rfq_id", "seller_id"}
    ),
    indexes = {
        @Index(name = "idx_rfq_chats_rfq_id",   columnList = "rfq_id"),
        @Index(name = "idx_rfq_chats_buyer_id",  columnList = "buyer_id"),
        @Index(name = "idx_rfq_chats_seller_id", columnList = "seller_id")
    }
)
public class RFQChat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private RFQ rfq;

    /** The buyer who owns the RFQ. Denormalised here for quick access. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    /** The seller participating in this negotiation thread. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    /** Set when the chat is locked (RFQ finalized, cancelled, expired). */
    @Column
    private Instant closedAt;

    public RFQChat() {}

    // ── Getters / Setters ─────────────────────────────────────────────────

    public UUID getId()             { return id; }
    public void setId(UUID id)      { this.id = id; }

    public RFQ getRfq()             { return rfq; }
    public void setRfq(RFQ rfq)     { this.rfq = rfq; }

    public User getBuyer()               { return buyer; }
    public void setBuyer(User buyer)     { this.buyer = buyer; }

    public User getSeller()              { return seller; }
    public void setSeller(User seller)   { this.seller = seller; }

    public boolean isActive()            { return active; }
    public void setActive(boolean a)     { this.active = a; }

    public Instant getCreatedAt()              { return createdAt; }
    public void setCreatedAt(Instant createdAt){ this.createdAt = createdAt; }

    public Instant getClosedAt()               { return closedAt; }
    public void setClosedAt(Instant closedAt)  { this.closedAt = closedAt; }
}
