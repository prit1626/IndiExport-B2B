package com.IndiExport.backend.exception;

public class CheckoutFailedException extends ApiException {
    public CheckoutFailedException(String reason) {
        super("CHECKOUT_FAILED", "Checkout failed: " + reason, 500);
    }

    public CheckoutFailedException(String reason, Throwable cause) {
        super("CHECKOUT_FAILED", "Checkout failed: " + reason + " (" + cause.getMessage() + ")", 500);
    }
}
