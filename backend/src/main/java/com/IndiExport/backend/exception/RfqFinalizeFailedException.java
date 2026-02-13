package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;

public class RfqFinalizeFailedException extends ApiException {
    public RfqFinalizeFailedException(String message) {
        super("RFQ_FINALIZE_FAILED", message, HttpStatus.BAD_REQUEST.value());
    }
}
