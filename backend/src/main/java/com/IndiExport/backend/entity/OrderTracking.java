package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Order tracking info — one per order.
 * Seller uploads courier + tracking number; buyer views status.
 */
@Entity
@Table(name = "order_tracking", indexes = {
        @Index(name = "idx_order_tracking_order_id", columnList = "order_id"),
        @Index(name = "idx_order_tracking_number", columnList = "tracking_number")
})
@NoArgsConstructor
public class OrderTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, length = 100)
    private String courierName;

    @Column(nullable = false, length = 100)
    private String trackingNumber;

    @Column(length = 500)
    private String trackingUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TrackingStatus currentStatus = TrackingStatus.SHIPPED;

    @Column(nullable = false)
    private Instant shippedAt;

    @Column
    private Instant deliveredAt;

    @OneToMany(mappedBy = "tracking", cascade = CascadeType.ALL, orphanRemoval = true,
               fetch = FetchType.LAZY)
    private List<OrderTrackingEvent> events = new ArrayList<>();

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getCourierName() { return courierName; }
    public void setCourierName(String courierName) { this.courierName = courierName; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getTrackingUrl() { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }
    public TrackingStatus getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(TrackingStatus currentStatus) { this.currentStatus = currentStatus; }
    public Instant getShippedAt() { return shippedAt; }
    public void setShippedAt(Instant shippedAt) { this.shippedAt = shippedAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; }
    public List<OrderTrackingEvent> getEvents() { return events; }
    public void setEvents(List<OrderTrackingEvent> events) { this.events = events; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
