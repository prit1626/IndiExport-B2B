package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.PaymentDto;
import com.IndiExport.backend.exception.WebhookSignatureInvalidException;
import com.IndiExport.backend.service.payment.StripePaymentProvider;
import com.IndiExport.backend.service.payment.WebhookProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Stripe webhook endpoint.
 * Publicly accessible (no JWT), but verified via Stripe signature.
 */
@RestController
@RequestMapping("/api/v1/payments/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final StripePaymentProvider stripeProvider;
    private final WebhookProcessingService webhookService;

    @PostMapping("/stripe")
    public ResponseEntity<PaymentDto.WebhookResponse> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {

        // 1. Verify signature
        if (!stripeProvider.verifyWebhookSignature(payload, signature)) {
            throw new WebhookSignatureInvalidException("STRIPE");
        }

        // 2. Process event
        webhookService.processStripeEvent(payload);

        return ResponseEntity.ok(PaymentDto.WebhookResponse.builder()
                .received(true)
                .message("Stripe event processed")
                .build());
    }
}
