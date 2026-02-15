package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "terms_versions", indexes = {
        @Index(name = "idx_terms_version_number", columnList = "version_number", unique = true),
        @Index(name = "idx_terms_is_published", columnList = "is_published")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermsVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private int versionNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private boolean isPublished = false;

    @Column
    private Instant publishedAt;

    @Column(nullable = false)
    private UUID createdByAdminId;

    @Column(nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
