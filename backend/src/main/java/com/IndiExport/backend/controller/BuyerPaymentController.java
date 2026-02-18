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
     * Creates or retrieves a Razorpay order.
     */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<PaymentDto.RazorpayOrderResponse> createPayment(
            @PathVariable UUID orderId) {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(paymentService.createRazorpayOrder(userId, orderId));
    }

    /**
     * POST /buyer/orders/{orderId}/verify
     * Verifies Razorpay payment signature.
     */
    @PostMapping("/{orderId}/verify")
    public ResponseEntity<Void> verifyPayment(
            @PathVariable UUID orderId,
            @RequestBody PaymentDto.RazorpayVerifyRequest request) {
        UUID userId = getCurrentUserId();
        // Ensure the orderId in path matches request if needed, or just set it
        request.setPlatformOrderId(orderId);
        paymentService.verifyRazorpayPayment(userId, request);
        return ResponseEntity.ok().build();
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
