package com.IndiExport.backend.exception;

public class InvalidPaymentStateException extends ApiException {
    public InvalidPaymentStateException(String currentState, String targetState) {
        super("INVALID_PAYMENT_STATE",
              "Cannot transition payment from " + currentState + " to " + targetState, 400);
    }
}
