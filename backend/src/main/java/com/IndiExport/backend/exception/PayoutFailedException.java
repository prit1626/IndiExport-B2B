package com.IndiExport.backend.exception;

public class PayoutFailedException extends ApiException {
    public PayoutFailedException(String reason) {
        super("PAYOUT_FAILED", "Payout failed: " + reason, 500);
    }
}
