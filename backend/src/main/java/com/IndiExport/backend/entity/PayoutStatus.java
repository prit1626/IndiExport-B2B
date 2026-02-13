package com.IndiExport.backend.entity;

/**
 * Payout status for seller payouts via RazorpayX.
 */
public enum PayoutStatus {
    CREATED,
    PROCESSING,
    SUCCESS,
    FAILED,
    REVERSED
}
