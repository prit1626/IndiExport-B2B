package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when user lacks permission to perform action (403).
 * Examples: Non-admin accessing admin panel, Seller accessing other seller's products
 */
public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(
            "FORBIDDEN",
            message,
            HttpStatus.FORBIDDEN.value()
        );
    }

    public ForbiddenException(String requiredRole, String action) {
        super(
            "FORBIDDEN",
            String.format("Role '%s' is required to %s", requiredRole, action),
            HttpStatus.FORBIDDEN.value()
        );
    }
}
