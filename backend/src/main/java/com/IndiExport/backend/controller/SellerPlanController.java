package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.PaymentResponse;
import com.IndiExport.backend.dto.RazorpayOrderResponse;
import com.IndiExport.backend.dto.PaymentVerificationRequest;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.SellerPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class SellerPlanController {

    private final SellerPlanService sellerPlanService;
    private final com.IndiExport.backend.service.admin.AdminSettingsService adminSettingsService;

    @GetMapping("/pricing")
    public ResponseEntity<Map<String, Long>> getPricing() {
        long price = adminSettingsService.getSettingsEntity().getAdvancedSellerPlanPriceInrPaise();
        return ResponseEntity.ok(Map.of("advancedPlanPricePaise", price));
    }

    @PostMapping("/upgrade/initiate")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<RazorpayOrderResponse> initiateUpgrade() {
        return ResponseEntity.ok(sellerPlanService.initiatePlanUpgrade(getCurrentUserId()));
    }

    @PostMapping("/upgrade/verify")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, String>> verifyUpgrade(@Valid @RequestBody PaymentVerificationRequest request) {
        sellerPlanService.verifyPlanUpgrade(getCurrentUserId(), request);
        return ResponseEntity.ok(Map.of("message", "Plan upgraded successfully"));
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object details = auth.getDetails();
        if (details instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            return UUID.fromString(((JwtAuthenticationFilter.JwtAuthenticationDetails) details).getUserId());
        }
        throw new IllegalStateException("User not authenticated");
    }
}
