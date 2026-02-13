package com.IndiExport.backend.exception;

public class UnauthorizedPaymentAccessException extends ApiException {
    public UnauthorizedPaymentAccessException() {
        super("UNAUTHORIZED_PAYMENT_ACCESS",
              "You don't have permission to access this payment", 403);
    }
}
