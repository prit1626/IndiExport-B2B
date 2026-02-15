package com.IndiExport.backend.service.chat;

import com.IndiExport.backend.dto.chat.StartChatResponse;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InquiryChatService {

    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final ProductRepository productRepository;

    @Transactional
    public StartChatResponse startInquiry(UUID buyerId, UUID productId) {
        BuyerProfile buyer = buyerProfileRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        SellerProfile seller = product.getSeller();
        
        // Check existing
        Optional<Chat> existing = chatRepository.findByBuyerIdAndSellerIdAndProductId(buyer.getId(), seller.getId(), productId);
        if (existing.isPresent()) {
            return mapToStartResponse(existing.get(), false);
        }
        
        // Create new
        Chat chat = new Chat();
        chat.setChatType(ChatType.INQUIRY_CHAT);
        chat.setBuyer(buyer);
        chat.setSeller(seller);
        chat.setProduct(product);
        chat.setStatus(ChatStatus.ACTIVE);
        
        chat = chatRepository.save(chat);
        
        // Add participants
        addParticipant(chat, buyer.getUser().getId(), Role.RoleType.BUYER);
        addParticipant(chat, seller.getUser().getId(), Role.RoleType.SELLER);
        
        return mapToStartResponse(chat, true);
    }
    
    private void addParticipant(Chat chat, UUID userId, Role.RoleType role) {
        ChatParticipant p = new ChatParticipant();
        p.setChat(chat);
        p.setUserId(userId);
        p.setRole(role);
        chatParticipantRepository.save(p);
    }
    
    private StartChatResponse mapToStartResponse(Chat chat, boolean isNew) {
        return StartChatResponse.builder()
                .chatId(chat.getId())
                .chatType(chat.getChatType())
                .status(chat.getStatus())
                .buyerId(chat.getBuyer().getId())
                .sellerId(chat.getSeller().getId())
                .productId(chat.getProduct().getId())
                .createdAt(chat.getCreatedAt())
                .isNew(isNew)
                .build();
    }
}
