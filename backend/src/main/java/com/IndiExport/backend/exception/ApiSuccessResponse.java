package com.IndiExport.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API success response structure returned to clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiSuccessResponse<T> {
    private boolean success;
    private String message;
    private int statusCode;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ApiSuccessResponse<T> of(T data, String message, int statusCode) {
        return ApiSuccessResponse.<T>builder()
                .success(true)
                .message(message)
                .statusCode(statusCode)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiSuccessResponse<T> of(T data) {
        return of(data, "Success", 200);
    }
}
