package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.TrackingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTOs for order tracking.
 */
public class TrackingDto {

    @NoArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Courier name is required")
        private String courierName;

        @NotBlank(message = "Tracking number is required")
        private String trackingNumber;

        private String trackingUrl;

        @NotNull(message = "Shipped-at timestamp is required")
        private Instant shippedAt;

        // Getters and Setters
        public String getCourierName() { return courierName; }
        public void setCourierName(String courierName) { this.courierName = courierName; }
        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
        public String getTrackingUrl() { return trackingUrl; }
        public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }
        public Instant getShippedAt() { return shippedAt; }
        public void setShippedAt(Instant shippedAt) { this.shippedAt = shippedAt; }
    }

    @NoArgsConstructor
    public static class EventRequest {
        @NotNull(message = "Status is required")
        private TrackingStatus status;

        private String location;
        private String message;

        @NotNull(message = "Event time is required")
        private Instant eventTime;

        // Getters and Setters
        public TrackingStatus getStatus() { return status; }
        public void setStatus(TrackingStatus status) { this.status = status; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Instant getEventTime() { return eventTime; }
        public void setEventTime(Instant eventTime) { this.eventTime = eventTime; }
    }

    @NoArgsConstructor
    public static class TrackingResponse {
        private UUID id;
        private UUID orderId;
        private String orderNumber;
        private String courierName;
        private String trackingNumber;
        private String trackingUrl;
        private TrackingStatus currentStatus;
        private Instant shippedAt;
        private Instant deliveredAt;
        private List<EventResponse> events;
        private Instant createdAt;

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getOrderId() { return orderId; }
        public void setOrderId(UUID orderId) { this.orderId = orderId; }
        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
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
        public List<EventResponse> getEvents() { return events; }
        public void setEvents(List<EventResponse> events) { this.events = events; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    @NoArgsConstructor
    public static class EventResponse {
        private UUID id;
        private TrackingStatus status;
        private String location;
        private String message;
        private Instant eventTime;

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public TrackingStatus getStatus() { return status; }
        public void setStatus(TrackingStatus status) { this.status = status; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Instant getEventTime() { return eventTime; }
        public void setEventTime(Instant eventTime) { this.eventTime = eventTime; }
    }
}
