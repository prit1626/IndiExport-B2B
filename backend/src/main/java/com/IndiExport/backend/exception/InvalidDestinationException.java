package com.IndiExport.backend.exception;

public class InvalidDestinationException extends ApiException {
    public InvalidDestinationException(String country) {
        super("INVALID_DESTINATION",
              "Invalid destination country code: '" + country + "'. Must be a valid ISO-2 country code.", 400);
    }
}
