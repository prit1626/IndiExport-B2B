package com.IndiExport.backend.exception;

public class PaymentProviderException extends ApiException {
    public PaymentProviderException(String provider, String detail) {
        super("PAYMENT_PROVIDER_ERROR",
              "Payment provider '" + provider + "' error: " + detail, 502);
    }
}
