package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an invoice is not found.
 */
public class InvoiceNotFoundException extends ApiException {
    public InvoiceNotFoundException(String message) {
        super(
            "INVOICE_NOT_FOUND",
            message,
            HttpStatus.NOT_FOUND.value()
        );
    }
}
