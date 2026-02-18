package com.IndiExport.backend.entity;

/**
 * Payment provider for buyer payments and seller payouts.
 */
public enum PaymentProvider {
    STRIPE,      // International buyer payments
    RAZORPAY,    // Indian buyer payments
    RAZORPAYX    // Indian seller payouts
}
