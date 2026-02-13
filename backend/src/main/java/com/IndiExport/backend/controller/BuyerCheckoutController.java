package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.CheckoutDto;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.CheckoutService;
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
 * Buyer checkout endpoint.
 * Converts cart into orders with locked prices, shipping quotes, and exchange rates.
 */
@RestController
@RequestMapping("/api/v1/buyer/checkout")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class BuyerCheckoutController {

    private final CheckoutService checkoutService;

    /**
     * POST /api/v1/buyer/checkout
     * Process checkout: validate cart → create orders → lock rates → clear cart.
     */
    @PostMapping
    public ResponseEntity<CheckoutDto.CheckoutResponse> checkout(
            @Valid @RequestBody CheckoutDto.CheckoutRequest request) {
        UUID userId = getCurrentUserId();
        CheckoutDto.CheckoutResponse response = checkoutService.checkout(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
