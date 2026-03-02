package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class PaymentFailedException extends ApiException {
    public PaymentFailedException(String message) {
        super("PAYMENT_FAILED", message, org.springframework.http.HttpStatus.PAYMENT_REQUIRED.value());
    }
}
