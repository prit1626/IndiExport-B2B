package com.IndiExport.backend.entity;

public enum DisputeResolutionAction {
    REFUND,             // Full refund to buyer
    PARTIAL_REFUND,     // Partial refund to buyer
    REPLACEMENT,        // Seller sends replacement
    REJECT              // Dispute rejected, funds released to seller
}
