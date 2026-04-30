package com.IndiExport.backend.dto.rfqchat;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AcceptProposalResponse {
    private UUID orderId;
    private UUID invoiceId;
    private String message;
    private boolean paymentRequired;
}
