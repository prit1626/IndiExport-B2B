package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;

public class RfqQuoteNotFoundException extends ApiException {
    public RfqQuoteNotFoundException(String message) {
        super("RFQ_QUOTE_NOT_FOUND", message, HttpStatus.NOT_FOUND.value());
    }
}
