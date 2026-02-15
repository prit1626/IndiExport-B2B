package com.IndiExport.backend.controller.chat;

import com.IndiExport.backend.dto.chat.StartChatResponse;
import com.IndiExport.backend.entity.SellerProfile;
import com.IndiExport.backend.repository.SellerProfileRepository;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.chat.RfqChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seller/rfq")
@RequiredArgsConstructor
public class RfqChatController {

    private final RfqChatService rfqChatService;
    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;

    @PostMapping("/{rfqId}/chat/start")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<StartChatResponse> startRfqChat(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID rfqId) {
        
        UUID userId = userRepository.findByEmail(userDetails.getUsername()).get().getId();
        SellerProfile seller = sellerProfileRepository.findByUserId(userId).get();
        
        return ResponseEntity.ok(rfqChatService.startRfqChat(seller.getId(), rfqId));
    }
}
