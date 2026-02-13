package com.IndiExport.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard API error response structure.
 * Format:
 * {
 *   "timestamp": "ISO_DATE_TIME",
 *   "status": 400,
 *   "error": "BAD_REQUEST",
 *   "message": "Validation failed",
 *   "path": "/api/v1/auth/signup/buyer",
 *   "details": [{"field":"email","message":"must be a well-formed email address"}]
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldError> details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        private String field;
        private String message;
    }
}
