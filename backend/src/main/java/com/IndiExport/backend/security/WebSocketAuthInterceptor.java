package com.IndiExport.backend.security;

import com.IndiExport.backend.entity.ChatParticipant;
import com.IndiExport.backend.repository.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final ChatParticipantRepository chatParticipantRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String dest = accessor.getDestination();
            // Expected format: /topic/chat/{chatId}
            if (dest != null && dest.startsWith("/topic/chat/")) {
                String chatIdStr = dest.substring("/topic/chat/".length());
                // Remove any sub-paths like /typing
                if (chatIdStr.contains("/")) {
                    chatIdStr = chatIdStr.substring(0, chatIdStr.indexOf("/"));
                }

                String userIdStr = (String) accessor.getSessionAttributes().get("userId");
                
                if (userIdStr == null) {
                   throw new IllegalArgumentException("Unauthenticated WebSocket session");
                }

                try {
                    UUID chatId = UUID.fromString(chatIdStr);
                    UUID userId = UUID.fromString(userIdStr);
                    
                    // DB Check: Is user a participant?
                    chatParticipantRepository.findByChatIdAndUserId(chatId, userId)
                            .orElseThrow(() -> new SecurityException("User not authorized for this chat topic"));
                    
                } catch (Exception e) {
                    log.error("WebSocket subscription denied: {}", e.getMessage());
                    throw new SecurityException("Subscription denied");
                }
            }
        }
        return message;
    }
}
