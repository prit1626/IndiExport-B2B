package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class DisputeExceptions {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class DisputeNotFoundException extends RuntimeException {
        public DisputeNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class DisputeAccessDeniedException extends RuntimeException {
        public DisputeAccessDeniedException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class DisputeAlreadyExistsException extends RuntimeException {
        public DisputeAlreadyExistsException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class DisputeNotAllowedException extends RuntimeException {
        public DisputeNotAllowedException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class DisputeResolutionException extends RuntimeException {
        public DisputeResolutionException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class EvidenceUploadNotAllowedException extends RuntimeException {
        public EvidenceUploadNotAllowedException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidRefundAmountException extends RuntimeException {
        public InvalidRefundAmountException(String message) {
            super(message);
        }
    }
}
