package com.IndiExport.backend.exception;

public class TrackingNotFoundException extends ApiException {
    public TrackingNotFoundException(String orderId) {
        super("TRACKING_NOT_FOUND", "No tracking info found for order: " + orderId, 404);
    }
}
