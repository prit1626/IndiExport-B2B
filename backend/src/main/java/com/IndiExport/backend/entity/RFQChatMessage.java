package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * A single message inside an RFQChat negotiation thread.
 */
@Entity
@Table(
    name = "rfq_chat_messages",
    indexes = {
        @Index(name = "idx_rfq_msg_chat_id",    columnList = "chat_id"),
        @Index(name = "idx_rfq_msg_sender_id",  columnList = "sender_id"),
        @Index(name = "idx_rfq_msg_created_at", columnList = "created_at")
    }
)
public class RFQChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private RFQChat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RFQChatMessageType messageType;

    @Column(columnDefinition = "TEXT")
    private String messageText;

    /** Cloudinary URL for ATTACHMENT messages. */
    @Column(columnDefinition = "TEXT")
    private String attachmentUrl;

    /** Original file name for attachments (display only). */
    @Column(length = 255)
    private String attachmentFileName;

    // ── PRICE_PROPOSAL fields ─────────────────────────────────────────────

    /** Price in minor currency unit (e.g. cents / paise). Null if not a proposal. */
    @Column
    private Long proposedPriceMinor;

    @Column(length = 3)
    private String currency;

    @Column
    private Integer leadTimeDays;

    /** True when the buyer has accepted this price proposal. */
    @Column(nullable = false)
    private boolean accepted = false;

    // ── Edit support ──────────────────────────────────────────────────────

    @Column(nullable = false)
    private boolean edited = false;

    @Column
    private Instant editedAt;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public RFQChatMessage() {}

    // ── Getters / Setters ─────────────────────────────────────────────────

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public RFQChat getChat() { return chat; }
    public void setChat(RFQChat chat) { this.chat = chat; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public RFQChatMessageType getMessageType() { return messageType; }
    public void setMessageType(RFQChatMessageType messageType) { this.messageType = messageType; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public String getAttachmentFileName() { return attachmentFileName; }
    public void setAttachmentFileName(String attachmentFileName) { this.attachmentFileName = attachmentFileName; }

    public Long getProposedPriceMinor() { return proposedPriceMinor; }
    public void setProposedPriceMinor(Long proposedPriceMinor) { this.proposedPriceMinor = proposedPriceMinor; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(Integer leadTimeDays) { this.leadTimeDays = leadTimeDays; }

    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }

    public boolean isEdited() { return edited; }
    public void setEdited(boolean edited) { this.edited = edited; }

    public Instant getEditedAt() { return editedAt; }
    public void setEditedAt(Instant editedAt) { this.editedAt = editedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
