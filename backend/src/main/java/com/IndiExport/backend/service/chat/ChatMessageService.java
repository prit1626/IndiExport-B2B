package com.IndiExport.backend.service.chat;

import com.IndiExport.backend.dto.chat.MessageResponse;
import com.IndiExport.backend.dto.chat.SendMessageRequest;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.ChatAccessDeniedException;
import com.IndiExport.backend.repository.ChatMessageRepository;
import com.IndiExport.backend.repository.ChatParticipantRepository;
import com.IndiExport.backend.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    @Transactional
    public MessageResponse sendMessage(UUID chatId, UUID senderId, Role.RoleType senderRole, SendMessageRequest request) {
        
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new com.IndiExport.backend.exception.ResourceNotFoundException("Chat not found"));
        
        // Verify participation
        if (senderRole == Role.RoleType.BUYER && !chat.getBuyer().getUser().getId().equals(senderId)) {
            throw new ChatAccessDeniedException("You are not the buyer in this chat");
        }
        if (senderRole == Role.RoleType.SELLER && !chat.getSeller().getUser().getId().equals(senderId)) {
            throw new ChatAccessDeniedException("You are not the seller in this chat");
        }
        
        ChatMessage message = new ChatMessage();
        message.setChat(chat);
        message.setSenderUserId(senderId);
        message.setSenderRole(senderRole);
        message.setMessageType(request.getMessageType());
        message.setMessageText(request.getMessageText());
        message.setFileUrl(request.getFileUrl());
        message.setFileName(request.getFileName());
        message.setFileMimeType(request.getFileMimeType());
        
        message = chatMessageRepository.save(message);
        
        chat.onUpdate(); // Update chat timestamp
        chatRepository.save(chat);
        
        return mapToResponse(message);
    }
    
    public Page<MessageResponse> getMessages(UUID chatId, UUID userId, Pageable pageable) {
        // Access check can be done here or in controller
        return chatMessageRepository.findByChatId(chatId, pageable).map(this::mapToResponse);
    }

    public MessageResponse mapToResponse(ChatMessage msg) {
        return MessageResponse.builder()
                .id(msg.getId())
                .chatId(msg.getChat().getId())
                .senderUserId(msg.getSenderUserId())
                .senderRole(msg.getSenderRole())
                .messageType(msg.getMessageType())
                .messageText(msg.getMessageText())
                .fileUrl(msg.getFileUrl())
                .fileName(msg.getFileName())
                .fileMimeType(msg.getFileMimeType())
                .priceInrPaise(msg.getPriceInrPaise())
                .leadTimeDays(msg.getLeadTimeDays())
                .shippingEstimateInrPaise(msg.getShippingEstimateInrPaise())
                .createdAt(msg.getCreatedAt())
                .build();
    }
}
