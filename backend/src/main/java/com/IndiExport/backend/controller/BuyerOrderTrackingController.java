package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.TrackingDto;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.OrderTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Buyer-facing tracking endpoint.
 * Buyer can view tracking details for orders they own.
 */
@RestController
@RequestMapping("/api/v1/buyer/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class BuyerOrderTrackingController {

    private final OrderTrackingService trackingService;

    /**
     * GET /api/v1/buyer/orders/{orderId}/tracking
     * View tracking details for a buyer's order.
     */
    @GetMapping("/{orderId}/tracking")
    public ResponseEntity<TrackingDto.TrackingResponse> getTracking(@PathVariable UUID orderId) {
        UUID userId = getCurrentUserId();
        TrackingDto.TrackingResponse response = trackingService.getTrackingForBuyer(userId, orderId);
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
