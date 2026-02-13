package com.IndiExport.backend.entity;

/**
 * Tracking status for order shipments.
 */
public enum TrackingStatus {
    SHIPPED,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    EXCEPTION
}
