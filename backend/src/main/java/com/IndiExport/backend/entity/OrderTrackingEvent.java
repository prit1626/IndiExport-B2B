package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Individual tracking event — many per OrderTracking.
 * Seller can manually add events; later can integrate courier APIs.
 */
@Entity
@Table(name = "order_tracking_events", indexes = {
        @Index(name = "idx_ote_tracking_id", columnList = "tracking_id"),
        @Index(name = "idx_ote_event_time", columnList = "event_time")
})
@NoArgsConstructor
public class OrderTrackingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_id", nullable = false)
    private OrderTracking tracking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TrackingStatus status;

    @Column(length = 200)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private Instant eventTime;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public OrderTracking getTracking() { return tracking; }
    public void setTracking(OrderTracking tracking) { this.tracking = tracking; }
    public TrackingStatus getStatus() { return status; }
    public void setStatus(TrackingStatus status) { this.status = status; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Instant getEventTime() { return eventTime; }
    public void setEventTime(Instant eventTime) { this.eventTime = eventTime; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
