package com.IndiExport.backend.exception;

public class ShippingQuoteFailedException extends ApiException {
    public ShippingQuoteFailedException(String reason) {
        super("SHIPPING_QUOTE_FAILED",
              "Failed to calculate shipping quote: " + reason, 503);
    }
}
