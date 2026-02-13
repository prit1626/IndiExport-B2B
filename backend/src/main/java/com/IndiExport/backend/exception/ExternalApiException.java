package com.IndiExport.backend.exception;

/**
 * Thrown when an external API call (e.g. exchange rate provider) fails.
 * HTTP 502 Bad Gateway.
 */
public class ExternalApiException extends ApiException {

    public ExternalApiException(String provider, String detail) {
        super("EXTERNAL_API_ERROR",
              "External API '" + provider + "' failed: " + detail,
              502);
    }

    public ExternalApiException(String provider, Throwable cause) {
        super("EXTERNAL_API_ERROR",
              "External API '" + provider + "' failed: " + cause.getMessage(),
              502);
    }
}
