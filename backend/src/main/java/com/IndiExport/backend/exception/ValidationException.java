package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when request validation fails (400).
 * Examples: Invalid email format, Exceeding product limit, Invalid currency
 */
public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(
            "BAD_REQUEST",
            message,
            HttpStatus.BAD_REQUEST.value()
        );
    }

    public ValidationException(String field, String reason) {
        super(
            "BAD_REQUEST",
            String.format("Validation failed for field '%s': %s", field, reason),
            HttpStatus.BAD_REQUEST.value()
        );
    }
}
