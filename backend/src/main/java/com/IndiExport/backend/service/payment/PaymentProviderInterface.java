package com.IndiExport.backend.service.payment;

import java.util.Map;

/**
 * Provider-agnostic payment interface.
 * Each provider implements this for buyer payment and/or seller payout operations.
 */
public interface PaymentProviderInterface {

    /**
     * Create a payment intent with the provider.
     *
     * @return map with keys: "paymentIntentId", "clientSecret"
     */
    Map<String, String> createPaymentIntent(long amountMinor, String currency,
                                             Map<String, String> metadata);

    /**
     * Verify webhook signature from the provider.
     *
     * @return true if signature is valid
     */
    boolean verifyWebhookSignature(String payload, String signatureHeader);
}
