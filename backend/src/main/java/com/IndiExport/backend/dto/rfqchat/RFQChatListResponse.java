package com.IndiExport.backend.dto.rfqchat;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class RFQChatListResponse {
    private UUID chatId;
    private UUID rfqId;
    private String rfqTitle;
    private int qty;
    private String unit;
    private String destinationCountry;
    /** Name of the other participant (seller sees buyer name, buyer sees seller name). */
    private String otherPartyName;
    private String lastMessagePreview;
    private Instant updatedAt;
    private long unreadCount;
    private boolean active;
}
