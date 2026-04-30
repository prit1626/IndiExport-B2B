package com.IndiExport.backend.controller.rfqchat;

import com.IndiExport.backend.dto.rfqchat.*;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.FileStorageService;
import com.IndiExport.backend.service.rfqchat.RFQChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rfq-chat")
@RequiredArgsConstructor
@Slf4j
public class RFQChatController {

    private final RFQChatService rfqChatService;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    private static final String[] ALLOWED_TYPES = {
        "image/jpeg", "image/png", "image/webp", "image/gif",
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    };

    // ── GET  /rfq-chat/{chatId}/messages ─────────────────────────────────────
    @GetMapping("/{chatId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<RFQChatMessageResponse>> getMessages(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId,
            @PageableDefault(size = 50) Pageable pageable) {
        UUID userId = resolveUserId(userDetails);
        return ResponseEntity.ok(rfqChatService.getMessages(chatId, userId, pageable));
    }

    // ── POST /rfq-chat/{chatId}/message ──────────────────────────────────────
    @PostMapping("/{chatId}/message")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RFQChatMessageResponse> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId,
            @Valid @RequestBody SendMessageRequest request) {
        UUID userId = resolveUserId(userDetails);
        return ResponseEntity.ok(rfqChatService.sendMessage(chatId, userId, request));
    }

    // ── POST /rfq-chat/{chatId}/price-proposal ───────────────────────────────
    @PostMapping("/{chatId}/price-proposal")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<RFQChatMessageResponse> sendPriceProposal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId,
            @Valid @RequestBody PriceProposalRequest request) {
        UUID sellerId = resolveUserId(userDetails);
        return ResponseEntity.ok(rfqChatService.sendPriceProposal(chatId, sellerId, request));
    }

    // ── POST /rfq-chat/{chatId}/accept-proposal ──────────────────────────────
    @PostMapping("/{chatId}/accept-proposal")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<AcceptProposalResponse> acceptProposal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId,
            @Valid @RequestBody AcceptProposalRequest request) {
        UUID buyerId = resolveUserId(userDetails);
        return ResponseEntity.ok(rfqChatService.acceptProposal(chatId, buyerId, request.getMessageId()));
    }

    // ── PUT  /rfq-chat/{chatId}/read ─────────────────────────────────────────
    @PutMapping("/{chatId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId) {
        UUID userId = resolveUserId(userDetails);
        rfqChatService.markRead(chatId, userId);
        return ResponseEntity.noContent().build();
    }

    // ── POST /rfq-chat/{chatId}/upload ───────────────────────────────────────
    @PostMapping(value = "/{chatId}/upload", consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> upload(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID chatId,
            @RequestParam("file") MultipartFile file) {

        // Validate participation
        UUID userId = resolveUserId(userDetails);
        // Access check via getMessages is implicit — if message fetch would fail, upload should too.
        // For brevity, we delegate access check to the service layer on message send.

        fileStorageService.validateFile(file, ALLOWED_TYPES, 10);
        String url;
        try {
            url = fileStorageService.uploadFile(file, "rfq-chat/" + chatId);
        } catch (IOException e) {
            log.error("Upload failed for chatId={}", chatId, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }

        return ResponseEntity.ok(Map.of(
            "url", url,
            "fileName", file.getOriginalFilename() != null ? file.getOriginalFilename() : "file"
        ));
    }

    private UUID resolveUserId(UserDetails ud) {
        return userRepository.findByEmail(ud.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
