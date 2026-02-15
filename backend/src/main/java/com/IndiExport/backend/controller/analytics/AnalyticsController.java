package com.IndiExport.backend.controller.analytics;

import com.IndiExport.backend.dto.analytics.*;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.analytics.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/buyer")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<BuyerDashboardAnalyticsResponse> getBuyerAnalytics(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {
        
        if (from == null) from = Instant.now().minus(30, ChronoUnit.DAYS);
        if (to == null) to = Instant.now();

        return ResponseEntity.ok(analyticsService.getBuyerAnalytics(getCurrentUserId(), from, to));
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerDashboardAnalyticsResponse> getSellerAnalytics(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {

        if (from == null) from = Instant.now().minus(30, ChronoUnit.DAYS);
        if (to == null) to = Instant.now();

        return ResponseEntity.ok(analyticsService.getSellerAnalytics(getCurrentUserId(), from, to));
    }

    @GetMapping("/seller/advanced")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<AdvancedSellerAnalyticsResponse> getAdvancedSellerAnalytics(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {

        if (from == null) from = Instant.now().minus(30, ChronoUnit.DAYS);
        if (to == null) to = Instant.now();

        return ResponseEntity.ok(analyticsService.getAdvancedSellerAnalytics(getCurrentUserId(), from, to));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardAnalyticsResponse> getAdminAnalytics(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {

        if (from == null) from = Instant.now().minus(30, ChronoUnit.DAYS);
        if (to == null) to = Instant.now();

        return ResponseEntity.ok(analyticsService.getAdminAnalytics(from, to));
    }

    /**
     * Track product view. 
     * Open to public. If authenticated, user ID is recorded.
     */
    @PostMapping("/views/{productId}")
    public ResponseEntity<Void> recordProductView(
            @PathVariable UUID productId,
            @RequestParam(required = false) String country) {
        
        UUID userId = null;
        try {
            userId = getCurrentUserId();
        } catch (Exception e) {
            // User not authenticated, proceed with null userId
        }

        analyticsService.recordProductView(productId, userId, country);
        return ResponseEntity.ok().build();
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            return UUID.fromString(((JwtAuthenticationFilter.JwtAuthenticationDetails) auth.getDetails()).getUserId());
        }
        throw new RuntimeException("User not authenticated");
    }
}
