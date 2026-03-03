package com.IndiExport.backend.controller.rfqchat;

import com.IndiExport.backend.dto.rfqchat.RFQChatListResponse;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.rfqchat.RFQChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
public class SellerRFQChatController {

    private final RFQChatService rfqChatService;
    private final UserRepository userRepository;

    /** Start (or return existing) chat for this RFQ. */
    @PostMapping("/rfq/{rfqId}/chat/start")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<RFQChatListResponse> startChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID rfqId) {
        UUID sellerId = resolveUserId(userDetails);
        return ResponseEntity.ok(rfqChatService.startChat(rfqId, sellerId));
    }

    /** List all chats for this seller. */
    @GetMapping("/rfq-chats")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<RFQChatListResponse>> getMyChats(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID sellerId = resolveUserId(userDetails);
        return ResponseEntity.ok(rfqChatService.getSellerChats(sellerId, pageable));
    }

    private UUID resolveUserId(UserDetails ud) {
        return userRepository.findByEmail(ud.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
