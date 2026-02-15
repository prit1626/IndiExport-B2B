package com.IndiExport.backend.dto.chat;

import com.IndiExport.backend.entity.ChatStatus;
import com.IndiExport.backend.entity.ChatType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ChatListItemResponse {
    private UUID chatId;
    private ChatType chatType;
    private ChatStatus status;
    
    private UUID otherParticipantuserId;
    private String otherParticipantName;
    private String otherParticipantCompanyName;
    
    private String topicTitle; // Product name or RFQ title
    private String topicImageUrl; // Product image or RFQ image
    
    private MessageResponse lastMessage;
    private int unreadCount;
    private Instant updatedAt;
}
