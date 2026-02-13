package com.IndiExport.backend.exception;

public class TrackingAccessDeniedException extends ApiException {
    public TrackingAccessDeniedException() {
        super("TRACKING_ACCESS_DENIED",
              "You don't have permission to access or modify this tracking information", 403);
    }
}
