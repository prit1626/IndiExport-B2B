package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.PaymentDto;
import com.IndiExport.backend.entity.Payment;
import com.IndiExport.backend.repository.PaymentRepository;
import com.IndiExport.backend.service.payment.PaymentService;
import com.IndiExport.backend.service.payment.PayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Admin payment management endpoints.
 */
@RestController
@RequestMapping("/api/v1/admin/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPaymentController {

    private final PaymentService paymentService;
    private final PayoutService payoutService;
    private final PaymentRepository paymentRepository;

    /**
     * GET /admin/payments — list all payments
     */
    @GetMapping
    public ResponseEntity<List<PaymentDto.AdminPaymentResponse>> listPayments() {
        List<Payment> payments = paymentRepository.findAll();
        List<PaymentDto.AdminPaymentResponse> response = payments.stream()
                .map(paymentService::mapToAdminResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /admin/payments/{paymentId} — get payment details
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto.AdminPaymentResponse> getPayment(
            @PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getAdminPaymentDetail(paymentId));
    }

    /**
     * PUT /admin/payments/{paymentId}/force-release — force payout release
     */
    @PutMapping("/{paymentId}/force-release")
    public ResponseEntity<PaymentDto.PayoutResponse> forceRelease(
            @PathVariable UUID paymentId) {
        return ResponseEntity.ok(payoutService.releasePayout(paymentId, true));
    }

    /**
     * PUT /admin/payments/{paymentId}/hold — freeze payout (dispute lock)
     */
    @PutMapping("/{paymentId}/hold")
    public ResponseEntity<String> holdPayment(@PathVariable UUID paymentId) {
        payoutService.adminHoldPayment(paymentId);
        return ResponseEntity.ok("Payment held successfully");
    }

    /**
     * PUT /admin/payments/{paymentId}/refund — approve refund
     */
    @PutMapping("/{paymentId}/refund")
    public ResponseEntity<String> refundPayment(@PathVariable UUID paymentId) {
        payoutService.adminRefundPayment(paymentId);
        return ResponseEntity.ok("Payment refunded successfully");
    }
}
