package com.IndiExport.backend.exception;

import lombok.Getter;

@Getter
public class PaymentGatewayException extends RuntimeException {
    private final String provider;

    public PaymentGatewayException(String message, String provider) {
        super(message);
        this.provider = provider;
    }
}
