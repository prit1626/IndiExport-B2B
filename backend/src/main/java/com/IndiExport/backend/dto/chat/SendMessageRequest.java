package com.IndiExport.backend.dto.chat;

import com.IndiExport.backend.entity.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {
    
    @NotNull
    private MessageType messageType; // TEXT, FILE, IMAGE
    
    private String messageText;
    
    private String fileUrl;
    
    private String fileName;
    
    private String fileMimeType;
}
