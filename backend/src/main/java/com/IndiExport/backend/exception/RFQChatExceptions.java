package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class RFQChatExceptions {

    @ResponseStatus(HttpStatus.LOCKED)                               // 423
    public static class RFQClosedException extends RuntimeException {
        public RFQClosedException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)                            // 403
    public static class UnauthorizedChatAccessException extends RuntimeException {
        public UnauthorizedChatAccessException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.CONFLICT)                             // 409
    public static class ProposalAlreadyAcceptedException extends RuntimeException {
        public ProposalAlreadyAcceptedException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)                 // 422
    public static class RFQExpiredException extends RuntimeException {
        public RFQExpiredException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)                            // 404
    public static class RFQChatNotFoundException extends RuntimeException {
        public RFQChatNotFoundException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)                            // 404
    public static class RFQChatMessageNotFoundException extends RuntimeException {
        public RFQChatMessageNotFoundException(String msg) { super(msg); }
    }
}
