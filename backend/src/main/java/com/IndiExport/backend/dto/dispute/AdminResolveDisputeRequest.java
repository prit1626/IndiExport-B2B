package com.IndiExport.backend.dto.dispute;

import com.IndiExport.backend.entity.DisputeResolutionAction;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminResolveDisputeRequest {

    @NotNull(message = "Resolution action is required")
    private DisputeResolutionAction resolutionAction;

    private String resolutionNotes;

    private Long partialRefundAmountMinor; // Required if action is PARTIAL_REFUND
}
