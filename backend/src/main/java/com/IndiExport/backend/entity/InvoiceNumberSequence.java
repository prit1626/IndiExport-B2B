package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity to manage invoice number sequences using database locking.
 * Key format: {TYPE}-{YEAR} (e.g., INV-2026, PRF-2026)
 */
@Entity
@Table(name = "invoice_sequences")
public class InvoiceNumberSequence {

    @Id
    @Column(name = "sequence_key", length = 50)
    private String sequenceKey; // PK

    @Column(nullable = false)
    private long currentVal;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public InvoiceNumberSequence() {}

    public InvoiceNumberSequence(String sequenceKey, long currentVal) {
        this.sequenceKey = sequenceKey;
        this.currentVal = currentVal;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getSequenceKey() { return sequenceKey; }
    public void setSequenceKey(String sequenceKey) { this.sequenceKey = sequenceKey; }

    public long getCurrentVal() { return currentVal; }
    public void setCurrentVal(long currentVal) { this.currentVal = currentVal; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
