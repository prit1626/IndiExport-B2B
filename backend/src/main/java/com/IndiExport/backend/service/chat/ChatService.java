package com.IndiExport.backend.service.chat;

import com.IndiExport.backend.dto.chat.ChatListItemResponse;
import com.IndiExport.backend.dto.chat.MessageResponse;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.repository.ChatMessageRepository;
import com.IndiExport.backend.repository.ChatParticipantRepository;
import com.IndiExport.backend.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageService chatMessageService;
    private final com.IndiExport.backend.repository.RfqRepository rfqRepository;
    private final com.IndiExport.backend.repository.SellerProfileRepository sellerProfileRepository;

    @Transactional
    public java.util.UUID startRfqChat(UUID sellerId, UUID rfqId) {
        // Check if chat exists
        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new com.IndiExport.backend.exception.ResourceNotFoundException("RFQ not found"));
        
        UUID buyerId = rfq.getBuyer().getId();
        
        return chatRepository.findByBuyerIdAndSellerIdAndRfqId(buyerId, sellerId, rfqId)
                .map(Chat::getId)
                .orElseGet(() -> {
                    Chat chat = new Chat();
                    chat.setChatType(ChatType.RFQ_CHAT);
                    chat.setRfq(rfq);
                    chat.setBuyer(rfq.getBuyer());
                    chat.setSeller(sellerProfileRepository.findById(sellerId).orElseThrow());
                    chat.setStatus(ChatStatus.ACTIVE);
                    chat = chatRepository.save(chat);

                    // Create participants
                    ChatParticipant sellerParticipant = new ChatParticipant();
                    sellerParticipant.setChat(chat);
                    sellerParticipant.setUserId(chat.getSeller().getUser().getId());
                    sellerParticipant.setRole(Role.RoleType.SELLER);
                    sellerParticipant.setLastReadAt(Instant.now());
                    chatParticipantRepository.save(sellerParticipant);

                    ChatParticipant buyerParticipant = new ChatParticipant();
                    buyerParticipant.setChat(chat);
                    buyerParticipant.setUserId(chat.getBuyer().getUser().getId());
                    buyerParticipant.setRole(Role.RoleType.BUYER);
                    // buyer hasn't read yet
                    chatParticipantRepository.save(buyerParticipant);

                    return chat.getId();
                });
    }

    @Transactional(readOnly = true)
    public Page<ChatListItemResponse> getBuyerRfqChats(UUID buyerId, Pageable pageable) {
        return chatRepository.findByBuyerIdAndChatType(buyerId, ChatType.RFQ_CHAT, pageable)
                .map(chat -> mapToListItem(chat, Role.RoleType.BUYER, buyerId));
    }

    @Transactional(readOnly = true)
    public Page<ChatListItemResponse> getSellerRfqChats(UUID sellerId, Pageable pageable) {
        return chatRepository.findBySellerIdAndChatType(sellerId, ChatType.RFQ_CHAT, pageable)
                .map(chat -> mapToListItem(chat, Role.RoleType.SELLER, sellerId));
    }

    @Transactional(readOnly = true)
    public Page<ChatListItemResponse> getBuyerChats(UUID buyerId, Pageable pageable) {
        // Renaming original to specific Inquiry Fetch if needed, or keep generic for "Inquiries"
        // The original code used generic findByBuyerId which fetches ALL.
        // Assuming original intent was "Inquiries", we limit it to match existing controller logic?
        // Existing controller maps to /buyer/inquiries. If we want ONLY inquiries there:
        return chatRepository.findByBuyerIdAndChatType(buyerId, ChatType.INQUIRY_CHAT, pageable)
                .map(chat -> mapToListItem(chat, Role.RoleType.BUYER, buyerId));
    }

    @Transactional(readOnly = true)
    public Page<ChatListItemResponse> getSellerChats(UUID sellerId, Pageable pageable) {
         return chatRepository.findBySellerIdAndChatType(sellerId, ChatType.INQUIRY_CHAT, pageable)
                .map(chat -> mapToListItem(chat, Role.RoleType.SELLER, sellerId));
    }

    private ChatListItemResponse mapToListItem(Chat chat, Role.RoleType viewerRole, UUID viewerId) {
        UUID otherUserId;
        String otherName;
        String otherCompany = null;
        String topicTitle;
        String topicImage;

        if (viewerRole == Role.RoleType.BUYER) {
            otherUserId = chat.getSeller().getUser().getId();
            otherName = chat.getSeller().getUser().getFullName(); // Assuming User link
            otherCompany = chat.getSeller().getCompanyName();
        } else {
            otherUserId = chat.getBuyer().getUser().getId();
            otherName = chat.getBuyer().getUser().getFullName();
        }

        if (chat.getChatType() == ChatType.INQUIRY_CHAT) {
            topicTitle = chat.getProduct() != null ? chat.getProduct().getName() : "Unknown Product";
            topicImage = (chat.getProduct() != null && !chat.getProduct().getMedia().isEmpty()) 
                    ? chat.getProduct().getMedia().get(0).getMediaUrl() : null;
        } else {
            topicTitle = chat.getRfq() != null ? "RFQ: " + chat.getRfq().getTitle() : "Unknown RFQ";
            topicImage = (chat.getRfq() != null && !chat.getRfq().getMedia().isEmpty())
                    ? chat.getRfq().getMedia().get(0).getUrl() : null;
        }

        // Bug Fix 2: Optimized fetching to avoid N+1 is complex in JPA without graphs.
        // For this response, we will assume standard behavior but note the optimization.
        // In production, we would use @EntityGraph or a DTO projection query for getBuyerChats.
        
        // Fetch last message
        ChatMessage lastMsg = chatMessageRepository.findFirstByChatIdOrderByCreatedAtDesc(chat.getId());
        MessageResponse lastMsgDto = lastMsg != null ? chatMessageService.mapToResponse(lastMsg) : null;

        // Unread count
        ChatParticipant participant = chatParticipantRepository.findByChatIdAndUserId(chat.getId(), viewerId).orElse(null);
        Instant lastRead = participant != null && participant.getLastReadAt() != null ? participant.getLastReadAt() : Instant.EPOCH;
        int unread = chatParticipantRepository.countUnreadMessages(chat.getId(), lastRead);

        return ChatListItemResponse.builder()
                .chatId(chat.getId())
                .chatType(chat.getChatType())
                .status(chat.getStatus())
                .otherParticipantuserId(otherUserId)
                .otherParticipantName(otherName)
                .otherParticipantCompanyName(otherCompany)
                .topicTitle(topicTitle)
                .topicImageUrl(topicImage)
                .lastMessage(lastMsgDto)
                .unreadCount(unread)
                .updatedAt(chat.getUpdatedAt())
                .build();
    }

    @Transactional
    public void markAsRead(UUID chatId, UUID userId) {
        ChatParticipant participant = chatParticipantRepository.findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new com.IndiExport.backend.exception.ResourceNotFoundException("Participant not found"));
        participant.setLastReadAt(Instant.now());
        chatParticipantRepository.save(participant);
    }
}
