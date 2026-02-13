package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Stores raw webhook payloads from payment providers for audit + idempotency.
 * Unique constraint on (provider, eventId) prevents duplicate processing.
 */
@Entity
@Table(name = "payment_webhook_events", indexes = {
        @Index(name = "idx_pwe_provider", columnList = "provider"),
        @Index(name = "idx_pwe_event_id", columnList = "event_id"),
        @Index(name = "idx_pwe_received_at", columnList = "received_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_pwe_provider_event", columnNames = {"provider", "event_id"})
})
@NoArgsConstructor
public class PaymentWebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentProvider provider;

    /** Provider-specific event ID (e.g. Stripe "evt_..." or Razorpay event ID). */
    @Column(name = "event_id", nullable = false, length = 200)
    private String eventId;

    /** Event type (e.g. "payment_intent.succeeded"). */
    @Column(nullable = false, length = 100)
    private String eventType;

    /** Raw JSON payload for audit trail. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Instant receivedAt = Instant.now();

    @Column(nullable = false)
    private boolean processed = false;

    @Column(columnDefinition = "TEXT")
    private String processingError;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public PaymentProvider getProvider() { return provider; }
    public void setProvider(PaymentProvider provider) { this.provider = provider; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public Instant getReceivedAt() { return receivedAt; }
    public void setReceivedAt(Instant receivedAt) { this.receivedAt = receivedAt; }
    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }
    public String getProcessingError() { return processingError; }
    public void setProcessingError(String processingError) { this.processingError = processingError; }
}
