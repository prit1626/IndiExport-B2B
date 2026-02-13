package com.IndiExport.backend.entity;

public enum RfqStatus {
    DRAFT,
    OPEN,               // Visible to sellers
    UNDER_NEGOTIATION,  // At least one quote received
    FINALIZED,          // Buyer accepted a quote
    CONVERTED_TO_ORDER, // Order created
    CANCELLED,          // Buyer cancelled
    EXPIRED             // Auto-expired
}
