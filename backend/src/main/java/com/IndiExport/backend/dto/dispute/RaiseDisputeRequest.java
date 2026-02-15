package com.IndiExport.backend.dto.dispute;

import com.IndiExport.backend.entity.DisputeReason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RaiseDisputeRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "Reason is required")
    private DisputeReason reason;

    @NotBlank(message = "Description is required")
    private String description;

    private List<String> evidenceUrls; // Optional initial evidence
}
