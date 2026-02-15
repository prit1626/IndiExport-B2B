package com.IndiExport.backend.entity;

public enum DisputeStatus {
    OPEN,               // Initial state, evidence gathering
    EVIDENCE_REQUIRED,  // Admin requested more info
    UNDER_REVIEW,       // Admin is reviewing
    RESOLVED,           // Admin made a decision (refund/replacement)
    REJECTED            // Admin rejected the claim
}
