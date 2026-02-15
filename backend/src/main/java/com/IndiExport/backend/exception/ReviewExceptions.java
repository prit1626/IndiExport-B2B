package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ReviewExceptions {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ReviewNotFoundException extends RuntimeException {
        public ReviewNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class ReviewAccessDeniedException extends RuntimeException {
        public ReviewAccessDeniedException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ReviewAlreadyExistsException extends RuntimeException {
        public ReviewAlreadyExistsException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class VerifiedPurchaseRequiredException extends RuntimeException {
        public VerifiedPurchaseRequiredException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidRatingException extends RuntimeException {
        public InvalidRatingException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ReviewReportAlreadyExistsException extends RuntimeException {
        public ReviewReportAlreadyExistsException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class ReviewModerationException extends RuntimeException {
        public ReviewModerationException(String message) {
            super(message);
        }
    }
}
