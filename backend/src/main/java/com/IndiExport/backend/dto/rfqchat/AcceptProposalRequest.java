package com.IndiExport.backend.dto.rfqchat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AcceptProposalRequest {
    @NotNull
    private UUID messageId;
}
