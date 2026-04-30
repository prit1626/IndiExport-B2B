package com.IndiExport.backend.dto.rfqchat;

import com.IndiExport.backend.entity.RFQChatMessageType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotNull
    private RFQChatMessageType messageType;
    private String messageText;
    private String attachmentUrl;
    private String attachmentFileName;
}
