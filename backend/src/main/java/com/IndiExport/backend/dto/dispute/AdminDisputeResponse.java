package com.IndiExport.backend.dto.dispute;

import com.IndiExport.backend.entity.DisputeReason;
import com.IndiExport.backend.entity.DisputeResolutionAction;
import com.IndiExport.backend.entity.DisputeStatus;
import com.IndiExport.backend.entity.Role.RoleType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AdminDisputeResponse {
    private UUID id;
    private UUID orderId;
    private String orderNumber;
    private UUID buyerId;
    private String buyerName;
    private UUID sellerId;
    private String companyName;
    private UUID raisedByUserId;
    private RoleType raisedByRole;
    private DisputeReason reason;
    private String description;
    private DisputeStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Admin specific
    private Instant resolvedAt;
    private UUID resolvedByAdminId;
    private DisputeResolutionAction resolutionAction;
    private String resolutionNotes;
    private Long partialRefundAmountMinor;
    
    private List<EvidenceResponse> evidence;
}
