package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import com.IndiExport.backend.entity.Role.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dispute_evidence")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisputeEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispute_id", nullable = false)
    private Dispute dispute;

    @Column(nullable = false)
    private UUID uploadedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleType uploadedByRole;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    @Column(length = 10)
    private String fileType; // IMAGE, DOC, PDF

    @Column(nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
