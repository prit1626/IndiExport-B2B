package com.IndiExport.backend.dto.rfqchat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PriceProposalRequest {
    @NotNull
    @Min(1)
    private Long proposedPriceMinor;

    @NotBlank
    private String currency; // e.g. "USD"

    @Min(1)
    private Integer leadTimeDays;
}
