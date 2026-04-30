package com.IndiExport.backend.dto.rfqchat;

import com.IndiExport.backend.entity.RFQChatMessageType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class RFQChatMessageResponse {
    private UUID id;
    private UUID chatId;
    private UUID senderId;
    private String senderName;    // firstName + lastName
    private String senderRole;    // "BUYER" or "SELLER"
    private RFQChatMessageType messageType;
    private String messageText;
    private String attachmentUrl;
    private String attachmentFileName;
    // PRICE_PROPOSAL fields
    private Long proposedPriceMinor;
    private String currency;
    private Integer leadTimeDays;
    private boolean accepted;
    // Edit metadata
    private boolean edited;
    private Instant editedAt;
    private Instant createdAt;
}
