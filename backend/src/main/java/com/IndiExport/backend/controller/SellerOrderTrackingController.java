package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.TrackingDto;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.OrderTrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Seller-facing tracking endpoints.
 * Seller can create tracking info, add events, and view tracking for their orders.
 */
@RestController
@RequestMapping("/api/v1/seller/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerOrderTrackingController {

    private final OrderTrackingService trackingService;

    /**
     * POST /api/v1/seller/orders/{orderId}/tracking
     * Upload tracking info for an order.
     */
    @PostMapping("/{orderId}/tracking")
    public ResponseEntity<TrackingDto.TrackingResponse> createTracking(
            @PathVariable UUID orderId,
            @Valid @RequestBody TrackingDto.CreateRequest request) {
        UUID userId = getCurrentUserId();
        TrackingDto.TrackingResponse response = trackingService.createTracking(userId, orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/v1/seller/orders/{orderId}/tracking/events
     * Add a tracking event (status update).
     */
    @PostMapping("/{orderId}/tracking/events")
    public ResponseEntity<TrackingDto.EventResponse> addEvent(
            @PathVariable UUID orderId,
            @Valid @RequestBody TrackingDto.EventRequest request) {
        UUID userId = getCurrentUserId();
        TrackingDto.EventResponse response = trackingService.addEvent(userId, orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/seller/orders/{orderId}/tracking
     * View tracking details for a seller's order.
     */
    @GetMapping("/{orderId}/tracking")
    public ResponseEntity<TrackingDto.TrackingResponse> getTracking(@PathVariable UUID orderId) {
        UUID userId = getCurrentUserId();
        TrackingDto.TrackingResponse response = trackingService.getTrackingForSeller(userId, orderId);
        return ResponseEntity.ok(response);
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object details = auth.getDetails();
        if (details instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            return UUID.fromString(
                    ((JwtAuthenticationFilter.JwtAuthenticationDetails) details).getUserId());
        }
        throw new IllegalStateException("User not authenticated");
    }
}
