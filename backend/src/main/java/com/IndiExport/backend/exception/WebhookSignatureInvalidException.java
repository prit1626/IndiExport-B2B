package com.IndiExport.backend.exception;

public class WebhookSignatureInvalidException extends ApiException {
    public WebhookSignatureInvalidException(String provider) {
        super("WEBHOOK_SIGNATURE_INVALID",
              "Webhook signature verification failed for provider: " + provider, 400);
    }
}
