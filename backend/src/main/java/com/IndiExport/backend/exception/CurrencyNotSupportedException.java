package com.IndiExport.backend.exception;

/**
 * Thrown when a currency code is not recognized or not supported by the system.
 * HTTP 400 Bad Request.
 */
public class CurrencyNotSupportedException extends ApiException {

    public CurrencyNotSupportedException(String currencyCode) {
        super("CURRENCY_NOT_SUPPORTED",
              "Currency '" + currencyCode + "' is not supported",
              400);
    }
}
