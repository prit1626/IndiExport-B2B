package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.PaymentRequest;
import com.IndiExport.backend.dto.RazorpayOrderResponse;
import com.IndiExport.backend.dto.PaymentVerificationRequest;
import com.IndiExport.backend.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<RazorpayOrderResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyPayment(@Valid @RequestBody PaymentVerificationRequest request) {
        paymentService.verifyPayment(request);
        return ResponseEntity.ok(Map.of("message", "Payment verified successfully"));
    }
    @GetMapping("/status/{orderId}")
    public ResponseEntity<com.IndiExport.backend.entity.PaymentStatus> getPaymentStatus(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.getPaymentStatus(orderId));
    }
}
