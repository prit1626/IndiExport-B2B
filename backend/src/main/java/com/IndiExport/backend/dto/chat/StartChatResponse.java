package com.IndiExport.backend.dto.chat;

import com.IndiExport.backend.entity.ChatStatus;
import com.IndiExport.backend.entity.ChatType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class StartChatResponse {
    private UUID chatId;
    private ChatType chatType;
    private ChatStatus status;
    private UUID buyerId;
    private UUID sellerId;
    private UUID productId;
    private UUID rfqId;
    private Instant createdAt;
    private boolean isNew; // True if created now, false if existed
}
