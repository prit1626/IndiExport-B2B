package com.IndiExport.backend.controller.analytics;

import com.IndiExport.backend.dto.analytics.AdvancedSellerAnalyticsResponse;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.analytics.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seller/advanced-analytics")
@RequiredArgsConstructor
public class SellerAdvancedAnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<AdvancedSellerAnalyticsResponse> getAdvancedAnalytics(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {

        if (from == null) from = Instant.now().minus(30, ChronoUnit.DAYS);
        if (to == null) to = Instant.now();

        return ResponseEntity.ok(analyticsService.getAdvancedSellerAnalytics(getCurrentUserId(), from, to));
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            return UUID.fromString(((JwtAuthenticationFilter.JwtAuthenticationDetails) auth.getDetails()).getUserId());
        }
        throw new RuntimeException("User not authenticated");
    }
}
