package com.IndiExport.backend.exception;

public class CartItemNotFoundException extends ApiException {
    public CartItemNotFoundException(String id) {
        super("CART_ITEM_NOT_FOUND", "Cart item not found: " + id, 404);
    }
}
