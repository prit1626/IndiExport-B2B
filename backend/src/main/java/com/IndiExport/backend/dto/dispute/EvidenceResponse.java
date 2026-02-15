package com.IndiExport.backend.dto.dispute;

import com.IndiExport.backend.entity.Role.RoleType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class EvidenceResponse {
    private UUID id;
    private UUID uploadedByUserId;
    private RoleType uploadedByRole;
    private String fileUrl;
    private String fileType;
    private Instant createdAt;
}
