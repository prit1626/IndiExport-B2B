package com.IndiExport.backend.controller.chat;

import com.IndiExport.backend.dto.chat.ChatListItemResponse;
import com.IndiExport.backend.dto.chat.MessageResponse;
import com.IndiExport.backend.dto.chat.SendMessageRequest;
import com.IndiExport.backend.entity.BuyerProfile;
import com.IndiExport.backend.entity.Role;
import com.IndiExport.backend.entity.SellerProfile;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.repository.BuyerProfileRepository;
import com.IndiExport.backend.repository.SellerProfileRepository;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.chat.ChatMessageService;
import com.IndiExport.backend.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/buyer/inquiries")
    public ResponseEntity<Page<ChatListItemResponse>> getBuyerChats(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(sort = "updatedAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        User user = getUser(userDetails);
        BuyerProfile buyer = buyerProfileRepository.findByUserId(user.getId()).orElseThrow();
        return ResponseEntity.ok(chatService.getBuyerChats(buyer.getId(), pageable));
    }

    @GetMapping("/seller/inquiries")
    public ResponseEntity<Page<ChatListItemResponse>> getSellerChats(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(sort = "updatedAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        User user = getUser(userDetails);
        SellerProfile seller = sellerProfileRepository.findByUserId(user.getId()).orElseThrow();
        return ResponseEntity.ok(chatService.getSellerChats(seller.getId(), pageable));
    }

    // === RFQ CHAT ENDPOINTS ===

    @PostMapping("/seller/rfq/{rfqId}/chat/start")
    public ResponseEntity<java.util.Map<String, UUID>> startRfqChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID rfqId) {
        
        User user = getUser(userDetails);
        SellerProfile seller = sellerProfileRepository.findByUserId(user.getId()).orElseThrow();
        UUID chatId = chatService.startRfqChat(seller.getId(), rfqId);
        return ResponseEntity.ok(java.util.Collections.singletonMap("chatId", chatId));
    }

    @GetMapping("/seller/rfq-chats")
    public ResponseEntity<Page<ChatListItemResponse>> getSellerRfqChats(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(sort = "updatedAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        User user = getUser(userDetails);
        SellerProfile seller = sellerProfileRepository.findByUserId(user.getId()).orElseThrow();
        return ResponseEntity.ok(chatService.getSellerRfqChats(seller.getId(), pageable));
    }

    @GetMapping("/buyer/rfq-chats")
    public ResponseEntity<Page<ChatListItemResponse>> getBuyerRfqChats(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(sort = "updatedAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        User user = getUser(userDetails);
        BuyerProfile buyer = buyerProfileRepository.findByUserId(user.getId()).orElseThrow();
        return ResponseEntity.ok(chatService.getBuyerRfqChats(buyer.getId(), pageable));
    }

    // === SHARED ===

    @PostMapping("/rfq-chat/{chatId}/message")
    public ResponseEntity<MessageResponse> sendRfqMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId,
            @RequestBody SendMessageRequest request) {
        return sendMessage(userDetails, chatId, request); // Reuse existing logic
    }

    @PostMapping("/chat/{chatId}/message")
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId,
            @RequestBody SendMessageRequest request) {
        
        User user = getUser(userDetails);
        // Determine role based on user's highest privilege or context. 
        // For simplicity, if they have SELLER role, assume SELLER, else BUYER.
        // Ideally, we should check which participant they are in the chat.
        Role.RoleType role = user.getRoles().stream()
                .anyMatch(r -> r.getName() == Role.RoleType.SELLER) 
                ? Role.RoleType.SELLER 
                : Role.RoleType.BUYER;
        
        MessageResponse response = chatMessageService.sendMessage(chatId, user.getId(), role, request);
        
        // Broadcast via WebSocket
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, response);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/chat/{chatId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId) {
        
        User user = getUser(userDetails);
        chatService.markAsRead(chatId, user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chat/{chatId}/messages")
    public ResponseEntity<Page<MessageResponse>> getMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId,
            @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        User user = getUser(userDetails);
        return ResponseEntity.ok(chatMessageService.getMessages(chatId, user.getId(), pageable));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }
}
