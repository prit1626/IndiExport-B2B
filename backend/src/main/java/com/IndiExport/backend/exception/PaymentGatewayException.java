package com.IndiExport.backend.exception;

import lombok.Getter;

@Getter
public class PaymentGatewayException extends ApiException {
    private final String provider;

    public PaymentGatewayException(String provider, String message) {
        super("PAYMENT_GATEWAY_ERROR", message, org.springframework.http.HttpStatus.BAD_GATEWAY.value());
        this.provider = provider;
    }
}
