package com.IndiExport.backend.dto.chat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class PriceProposalRequest {
    
    @NotNull
    @Min(1)
    private Long customPriceInrPaise;
    
    private Integer leadTimeDays;
    
    private Long shippingEstimateInrPaise;
}
