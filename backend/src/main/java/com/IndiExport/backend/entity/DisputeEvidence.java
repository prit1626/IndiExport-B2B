package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DisputeEvidence entity for storing evidence files related to disputes.
 * Evidence is immutable - cannot be updated or deleted once attached.
 */
@Entity
@Table(name = "dispute_evidence", indexes = {
        @Index(name = "idx_dispute_evidence_dispute_id", columnList = "dispute_id"),
        @Index(name = "idx_dispute_evidence_uploaded_by", columnList = "uploaded_by")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisputeEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispute_id", nullable = false)
    private Dispute dispute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String evidenceUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EvidenceType evidenceType; // 'PHOTO', 'VIDEO', 'DOCUMENT'

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public enum EvidenceType {
        PHOTO,
        VIDEO,
        DOCUMENT,
        CHAT_SCREENSHOT
    }
}
