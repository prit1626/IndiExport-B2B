package com.IndiExport.backend.exception;

public class PaymentAlreadyExistsException extends ApiException {
    public PaymentAlreadyExistsException(String orderId) {
        super("PAYMENT_ALREADY_EXISTS",
              "An active payment already exists for order: " + orderId, 409);
    }
}
