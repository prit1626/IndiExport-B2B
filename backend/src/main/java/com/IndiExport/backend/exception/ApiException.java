package com.IndiExport.backend.exception;

import lombok.Getter;

/**
 * Base custom exception for all API-level errors.
 * Subclasses: ResourceNotFoundException, ConflictException, ForbiddenException, UnauthorizedException, etc.
 */
@Getter
public class ApiException extends RuntimeException {
    private final String error;
    private final int status;

    public ApiException(String error, String message, int status) {
        super(message);
        this.error = error;
        this.status = status;
    }
}
