package com.IndiExport.backend.dto.dispute;

import com.IndiExport.backend.entity.DisputeReason;
import com.IndiExport.backend.entity.DisputeStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class DisputeListResponse {
    private UUID id;
    private UUID orderId;
    private String orderNumber;
    private DisputeReason reason;
    private DisputeStatus status;
    private Instant createdAt;
}
