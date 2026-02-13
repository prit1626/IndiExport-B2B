package com.IndiExport.backend.exception;

/**
 * Thrown when exchange rates cannot be fetched and no cached fallback is available.
 * HTTP 503 Service Unavailable.
 */
public class ExchangeRateUnavailableException extends ApiException {

    public ExchangeRateUnavailableException(String currency) {
        super("EXCHANGE_RATE_UNAVAILABLE",
              "Exchange rate for currency '" + currency + "' is currently unavailable. Please try again later.",
              503);
    }

    public ExchangeRateUnavailableException(String currency, Throwable cause) {
        super("EXCHANGE_RATE_UNAVAILABLE",
              "Exchange rate for currency '" + currency + "' is currently unavailable: " + cause.getMessage(),
              503);
    }
}
