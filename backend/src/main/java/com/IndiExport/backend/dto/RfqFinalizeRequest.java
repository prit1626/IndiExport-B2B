package com.IndiExport.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfqFinalizeRequest {
    @NotNull(message = "Quote ID is required")
    private UUID quoteId;
}
