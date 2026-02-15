package com.IndiExport.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.IndiExport.backend.exception.ReviewExceptions;
import com.IndiExport.backend.exception.DisputeExceptions;
import com.IndiExport.backend.exception.AdminExceptions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for all controllers.
 * All errors return the consistent ApiErrorResponse format.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle custom ApiException and all subclasses
     * (ResourceNotFoundException, ConflictException, ForbiddenException, UnauthorizedException, etc.)
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getStatus())
                .error(ex.getError())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatus()));
    }

    /**
     * Handle validation errors from @Valid annotation.
     * Returns details as List of {field, message} per the project spec.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ApiErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> ApiErrorResponse.FieldError.builder()
                        .field(err.getField())
                        .message(err.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("BAD_REQUEST")
                .message("Validation failed")
                .path(request.getRequestURI())
                .details(fieldErrors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle Spring Security access denied
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("FORBIDDEN")
                .message("Access denied: " + ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle bad credentials from Spring Security AuthenticationManager
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("UNAUTHORIZED")
                .message("Invalid email or password")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ChatAccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleChatAccessDeniedException(
            ChatAccessDeniedException ex, HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("FORBIDDEN")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // --- Review Exception Handlers ---

    @ExceptionHandler(ReviewExceptions.ReviewNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleReviewNotFound(ReviewExceptions.ReviewNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ReviewExceptions.ReviewAccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleReviewAccessDenied(ReviewExceptions.ReviewAccessDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler({
            ReviewExceptions.ReviewAlreadyExistsException.class,
            ReviewExceptions.ReviewReportAlreadyExistsException.class
    })
    public ResponseEntity<ApiErrorResponse> handleReviewConflicts(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({
            ReviewExceptions.VerifiedPurchaseRequiredException.class,
            ReviewExceptions.InvalidRatingException.class,
            ReviewExceptions.ReviewModerationException.class
    })
    public ResponseEntity<ApiErrorResponse> handleReviewBadRequest(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    // --- Dispute Exception Handlers ---

    @ExceptionHandler(DisputeExceptions.DisputeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleDisputeNotFound(DisputeExceptions.DisputeNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(DisputeExceptions.DisputeAccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleDisputeAccessDenied(DisputeExceptions.DisputeAccessDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, request);
    }

    // --- Admin & Terms Exception Handlers ---

    @ExceptionHandler({
            AdminExceptions.SettingsNotFoundException.class,
            AdminExceptions.TermsNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleAdminNotFound(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AdminExceptions.TermsAlreadyPublishedException.class)
    public ResponseEntity<ApiErrorResponse> handleAdminConflict(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(DisputeExceptions.DisputeAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleDisputeConflict(DisputeExceptions.DisputeAlreadyExistsException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({
            DisputeExceptions.DisputeNotAllowedException.class,
            DisputeExceptions.DisputeResolutionException.class,
            DisputeExceptions.EvidenceUploadNotAllowedException.class,
            DisputeExceptions.InvalidRefundAmountException.class
    })
    public ResponseEntity<ApiErrorResponse> handleDisputeBadRequest(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    // --- Analytics Exception Handlers ---

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidDateRange(InvalidDateRangeException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AnalyticsAccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAnalyticsAccessDenied(AnalyticsAccessDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, request);
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(RuntimeException ex, HttpStatus status, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.name())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Handle all other unhandled exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(
            Exception ex, HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
