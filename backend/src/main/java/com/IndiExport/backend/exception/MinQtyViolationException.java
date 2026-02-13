package com.IndiExport.backend.exception;

public class MinQtyViolationException extends ApiException {
    public MinQtyViolationException(String productName, int requested, int minimum) {
        super("MIN_QTY_VIOLATION",
              "Quantity " + requested + " for '" + productName +
              "' is below minimum order quantity of " + minimum, 400);
    }
}
