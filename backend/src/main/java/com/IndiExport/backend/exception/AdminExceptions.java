package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AdminExceptions {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class SettingsNotFoundException extends RuntimeException {
        public SettingsNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class TermsNotFoundException extends RuntimeException {
        public TermsNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class TermsAlreadyPublishedException extends RuntimeException {
        public TermsAlreadyPublishedException(String message) {
            super(message);
        }
    }
}
