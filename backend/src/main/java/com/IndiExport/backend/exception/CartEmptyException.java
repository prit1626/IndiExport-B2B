package com.IndiExport.backend.exception;

public class CartEmptyException extends ApiException {
    public CartEmptyException() {
        super("CART_EMPTY", "Cannot checkout with an empty cart", 400);
    }
}
