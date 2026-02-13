package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.PaymentDto;
import com.IndiExport.backend.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Buyer-facing payment endpoints.
 */
@RestController
@RequestMapping("/api/v1/buyer/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class BuyerPaymentController {

    private final PaymentService paymentService;

    /**
     * POST /buyer/orders/{orderId}/pay
     * Creates or retrieves a payment intent for Stripe checkout.
     */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<PaymentDto.CreatePaymentResponse> createPayment(
            @PathVariable UUID orderId) {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(paymentService.createPaymentIntent(userId, orderId));
    }

    /**
     * GET /buyer/orders/{orderId}/payment
     * Get the current payment status for an order.
     */
    @GetMapping("/{orderId}/payment")
    public ResponseEntity<PaymentDto.PaymentStatusResponse> getPaymentStatus(
            @PathVariable UUID orderId) {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(paymentService.getPaymentStatus(userId, orderId));
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(auth.getName());
    }
}
