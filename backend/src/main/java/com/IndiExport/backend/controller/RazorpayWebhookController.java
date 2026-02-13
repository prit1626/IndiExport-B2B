package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.PaymentDto;
import com.IndiExport.backend.exception.WebhookSignatureInvalidException;
import com.IndiExport.backend.service.payment.RazorpayXPayoutProvider;
import com.IndiExport.backend.service.payment.WebhookProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RazorpayX webhook endpoint for payout status updates.
 * Publicly accessible (no JWT), but verified via Razorpay signature.
 */
@RestController
@RequestMapping("/api/v1/payments/webhook")
@RequiredArgsConstructor
public class RazorpayWebhookController {

    private final RazorpayXPayoutProvider razorpayXProvider;
    private final WebhookProcessingService webhookService;

    @Value("${razorpayx.key-secret}")
    private String razorpayWebhookSecret;

    @PostMapping("/razorpay")
    public ResponseEntity<PaymentDto.WebhookResponse> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        // 1. Verify signature
        if (!razorpayXProvider.verifyWebhookSignature(payload, signature, razorpayWebhookSecret)) {
            throw new WebhookSignatureInvalidException("RAZORPAY");
        }

        // 2. Process event
        webhookService.processRazorpayEvent(payload);

        return ResponseEntity.ok(PaymentDto.WebhookResponse.builder()
                .received(true)
                .message("RazorpayX event processed")
                .build());
    }
}
