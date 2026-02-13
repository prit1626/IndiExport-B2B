package com.IndiExport.backend.exception;

public class InvalidShippingModeException extends ApiException {
    public InvalidShippingModeException(String mode) {
        super("INVALID_SHIPPING_MODE",
              "Invalid shipping mode: '" + mode + "'. Must be AIR, SEA, ROAD, or COURIER", 400);
    }
}
