package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a requested resource is not found (404).
 * Examples: User not found, Product not found, Order not found
 */
public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(
            "RESOURCE_NOT_FOUND",
            String.format("%s not found with identifier: %s", resourceType, identifier),
            HttpStatus.NOT_FOUND.value()
        );
    }

    public ResourceNotFoundException(String resourceType, String field, Object value) {
        super(
            "RESOURCE_NOT_FOUND",
            String.format("%s not found with %s: %s", resourceType, field, value),
            HttpStatus.NOT_FOUND.value()
        );
    }

    public ResourceNotFoundException(String message) {
        super(
            "RESOURCE_NOT_FOUND",
            message,
            HttpStatus.NOT_FOUND.value()
        );
    }
}
