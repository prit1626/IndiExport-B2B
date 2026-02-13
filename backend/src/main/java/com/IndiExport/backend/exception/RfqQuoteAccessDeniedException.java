package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;

public class RfqQuoteAccessDeniedException extends ApiException {
    public RfqQuoteAccessDeniedException(String message) {
        super("RFQ_QUOTE_ACCESS_DENIED", message, HttpStatus.FORBIDDEN.value());
    }
}
