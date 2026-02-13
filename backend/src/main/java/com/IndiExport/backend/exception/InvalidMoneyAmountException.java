package com.IndiExport.backend.exception;

/**
 * Thrown when a money amount is invalid (e.g. negative or zero when positive is required).
 * HTTP 400 Bad Request.
 */
public class InvalidMoneyAmountException extends ApiException {

    public InvalidMoneyAmountException(long amount) {
        super("INVALID_MONEY_AMOUNT",
              "Invalid money amount: " + amount + ". Amount must be a positive integer in minor units (e.g. paise).",
              400);
    }

    public InvalidMoneyAmountException(String message) {
        super("INVALID_MONEY_AMOUNT", message, 400);
    }
}
