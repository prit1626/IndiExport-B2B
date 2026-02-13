package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;

public class InvalidRfqStateException extends ApiException {
    public InvalidRfqStateException(String message) {
        super("INVALID_RFQ_STATE", message, HttpStatus.CONFLICT.value());
    }
}
