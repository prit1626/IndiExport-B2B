package com.IndiExport.backend.exception;

public class ShippingTemplateNotFoundException extends ApiException {
    public ShippingTemplateNotFoundException(String id) {
        super("SHIPPING_TEMPLATE_NOT_FOUND", "Shipping template not found: " + id, 404);
    }
}
