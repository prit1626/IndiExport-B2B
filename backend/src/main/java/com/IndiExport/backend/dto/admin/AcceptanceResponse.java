package com.IndiExport.backend.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class AcceptanceResponse {
    private UUID id;
    private UUID userId;
    private UUID termsVersionId;
    private int termsVersionNumber;
    private Instant acceptedAt;
}
