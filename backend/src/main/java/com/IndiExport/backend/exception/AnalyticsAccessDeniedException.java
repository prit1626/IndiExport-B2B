package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AnalyticsAccessDeniedException extends RuntimeException {
    public AnalyticsAccessDeniedException(String message) {
        super(message);
    }
}
