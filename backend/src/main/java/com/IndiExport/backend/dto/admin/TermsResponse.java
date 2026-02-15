package com.IndiExport.backend.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class TermsResponse {
    private UUID id;
    private int versionNumber;
    private String title;
    private String content;
    private boolean isPublished;
    private Instant publishedAt;
    private Instant createdAt;
}
