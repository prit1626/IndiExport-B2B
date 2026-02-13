package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ProductExceptions {

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ActiveProductLimitExceededException extends BusinessRuleViolationException {
        public ActiveProductLimitExceededException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class UnauthorizedProductAccessException extends ForbiddenException {
        public UnauthorizedProductAccessException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidFilterException extends ValidationException {
        public InvalidFilterException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class MediaUploadException extends ApiException {
        public MediaUploadException(String message) {
            super("MEDIA_UPLOAD_ERROR", message, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
