package com.IndiExport.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when business rule constraints are violated (422 Unprocessable Entity).
 * Examples: Seller exceeding product limit, Invalid status transition, RFQ already converted
 */
public class BusinessRuleViolationException extends ApiException {
    public BusinessRuleViolationException(String message) {
        super(
            "BUSINESS_RULE_VIOLATION",
            message,
            HttpStatus.UNPROCESSABLE_ENTITY.value()
        );
    }

    public BusinessRuleViolationException(String rule, String reason) {
        super(
            "BUSINESS_RULE_VIOLATION",
            String.format("Business rule violated: %s. Reason: %s", rule, reason),
            HttpStatus.UNPROCESSABLE_ENTITY.value()
        );
    }
}
