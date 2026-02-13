package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;

public class RfqAccessDeniedException extends ApiException {
    public RfqAccessDeniedException(String message) {
        super("RFQ_ACCESS_DENIED", message, HttpStatus.FORBIDDEN.value());
    }
}
