package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when user authentication fails or token is invalid (401).
 */
public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
        super(
            "UNAUTHORIZED",
            message,
            HttpStatus.UNAUTHORIZED.value()
        );
    }

    public UnauthorizedException() {
        super(
            "UNAUTHORIZED",
            "Authentication failed. Invalid or expired token.",
            HttpStatus.UNAUTHORIZED.value()
        );
    }
}
