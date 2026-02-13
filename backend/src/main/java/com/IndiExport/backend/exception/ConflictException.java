package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a resource conflict occurs (409).
 * Examples: Email already exists, Username already taken, Duplicate RFQ
 */
public class ConflictException extends ApiException {
    public ConflictException(String resourceType, String field, Object value) {
        super(
            "CONFLICT",
            String.format("%s with %s '%s' already exists", resourceType, field, value),
            HttpStatus.CONFLICT.value()
        );
    }

    public ConflictException(String message) {
        super(
            "CONFLICT",
            message,
            HttpStatus.CONFLICT.value()
        );
    }
}
