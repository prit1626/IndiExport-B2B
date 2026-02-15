package com.IndiExport.backend.controller.chat;

import com.IndiExport.backend.dto.chat.StartChatResponse;
import com.IndiExport.backend.entity.BuyerProfile;
import com.IndiExport.backend.repository.BuyerProfileRepository;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.chat.InquiryChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buyer/products")
@RequiredArgsConstructor
public class InquiryChatController {

    private final InquiryChatService inquiryChatService;
    private final UserRepository userRepository;
    private final BuyerProfileRepository buyerProfileRepository;

    @PostMapping("/{productId}/inquiry/start")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<StartChatResponse> startInquiry(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID productId) {
        
        UUID userId = userRepository.findByEmail(userDetails.getUsername()).get().getId();
        BuyerProfile buyer = buyerProfileRepository.findByUserId(userId).get();
        
        return ResponseEntity.ok(inquiryChatService.startInquiry(buyer.getId(), productId));
    }
}
