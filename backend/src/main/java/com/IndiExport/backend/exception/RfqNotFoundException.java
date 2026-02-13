package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class RfqNotFoundException extends ApiException {
    public RfqNotFoundException(String message) {
        super("RFQ_NOT_FOUND", message, HttpStatus.NOT_FOUND.value());
    }
}
