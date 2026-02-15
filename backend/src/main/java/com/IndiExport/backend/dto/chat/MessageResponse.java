package com.IndiExport.backend.dto.chat;

import com.IndiExport.backend.entity.MessageType;
import com.IndiExport.backend.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class MessageResponse {
    private UUID id;
    private UUID chatId;
    private UUID senderUserId;
    private Role.RoleType senderRole;
    private MessageType messageType;
    private String messageText;
    private String fileUrl;
    private String fileName;
    private String fileMimeType;
    private Long priceInrPaise;
    private Integer leadTimeDays;
    private Long shippingEstimateInrPaise;
    private Instant createdAt;
}
