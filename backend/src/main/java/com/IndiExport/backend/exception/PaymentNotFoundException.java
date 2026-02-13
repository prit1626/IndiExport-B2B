package com.IndiExport.backend.exception;

public class PaymentNotFoundException extends ApiException {
    public PaymentNotFoundException(String id) {
        super("PAYMENT_NOT_FOUND", "Payment not found: " + id, 404);
    }
}
