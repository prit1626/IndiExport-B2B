package com.IndiExport.backend.exception;

public class ProductNotActiveException extends ApiException {
    public ProductNotActiveException(String productName) {
        super("PRODUCT_NOT_ACTIVE",
              "Product '" + productName + "' is no longer active and cannot be purchased", 409);
    }
}
