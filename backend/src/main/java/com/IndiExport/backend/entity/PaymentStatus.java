package com.IndiExport.backend.entity;

/**
 * Payment lifecycle status.
 *
 * CREATED   → payment intent created, buyer hasn't paid yet
 * CAPTURED  → provider confirmed money captured (transient — immediately moves to HOLDING)
 * HOLDING   → platform escrow hold until delivery confirmation
 * RELEASED  → seller payout sent successfully
 * REFUNDED  → refund completed to buyer
 * FAILED    → payment failed or cancelled
 */
public enum PaymentStatus {
    CREATED,
    CAPTURED,
    HOLDING,
    RELEASED,
    REFUNDED,
    FAILED
}
