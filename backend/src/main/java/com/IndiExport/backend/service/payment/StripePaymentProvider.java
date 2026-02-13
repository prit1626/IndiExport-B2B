package com.IndiExport.backend.service.payment;

import com.IndiExport.backend.exception.PaymentProviderException;
import com.IndiExport.backend.exception.WebhookSignatureInvalidException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Stripe implementation for international buyer payments.
 */
@Service
public class StripePaymentProvider implements PaymentProviderInterface {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public Map<String, String> createPaymentIntent(long amountMinor, String currency,
                                                    Map<String, String> metadata) {
        try {
            PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
                    .setAmount(amountMinor)
                    .setCurrency(currency.toLowerCase())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    );

            if (metadata != null) {
                metadata.forEach(builder::putMetadata);
            }

            PaymentIntent intent = PaymentIntent.create(builder.build());

            Map<String, String> result = new HashMap<>();
            result.put("paymentIntentId", intent.getId());
            result.put("clientSecret", intent.getClientSecret());
            return result;

        } catch (StripeException e) {
            throw new PaymentProviderException("STRIPE", e.getMessage());
        }
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signatureHeader) {
        try {
            Webhook.constructEvent(payload, signatureHeader, webhookSecret);
            return true;
        } catch (SignatureVerificationException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
