package com.IndiExport.backend.controller.dispute;

import com.IndiExport.backend.dto.dispute.AddEvidenceRequest;
import com.IndiExport.backend.dto.dispute.DisputeListResponse;
import com.IndiExport.backend.dto.dispute.DisputeResponse;
import com.IndiExport.backend.dto.dispute.EvidenceResponse;
import com.IndiExport.backend.dto.dispute.RaiseDisputeRequest;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.dispute.DisputeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buyer/disputes")
@RequiredArgsConstructor
public class BuyerDisputeController {

    private final DisputeService disputeService;
    private final UserRepository userRepository;
    private final com.IndiExport.backend.service.FileStorageService fileStorageService;
    private final com.IndiExport.backend.repository.BuyerProfileRepository buyerProfileRepository;

    @PostMapping(consumes = { "multipart/form-data" })
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<DisputeResponse> raiseDispute(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("orderId") String orderId,
            @RequestPart("reason") String reason,
            @RequestPart("description") String description,
            @RequestPart(value = "files", required = false) org.springframework.web.multipart.MultipartFile[] files) {

        User user = getUser(userDetails);

        java.util.List<String> evidenceUrls = new java.util.ArrayList<>();
        if (files != null) {
            for (org.springframework.web.multipart.MultipartFile file : files) {
                try {
                    String url = fileStorageService.uploadFile(file, "disputes/" + orderId);
                    evidenceUrls.add(url);
                } catch (java.io.IOException e) {
                    throw new RuntimeException("Failed to upload evidence", e);
                }
            }
        }

        RaiseDisputeRequest request = RaiseDisputeRequest.builder()
                .orderId(UUID.fromString(orderId))
                .reason(com.IndiExport.backend.entity.DisputeReason.valueOf(reason))
                .description(description)
                .evidenceUrls(evidenceUrls)
                .build();

        return ResponseEntity.ok(disputeService.raiseDispute(user.getId(), request));
    }

    @GetMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Page<DisputeListResponse>> getMyDisputes(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) com.IndiExport.backend.entity.DisputeStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        User user = getUser(userDetails);

        return ResponseEntity.ok(disputeService.getDisputesForBuyer(getBuyerId(user), status, pageable));
    }

    @PostMapping(value = "/{disputeId}/evidence", consumes = { "multipart/form-data" })
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<EvidenceResponse> addEvidence(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID disputeId,
            @RequestPart("file") org.springframework.web.multipart.MultipartFile file) {
        User user = getUser(userDetails);

        String url;
        try {
            url = fileStorageService.uploadFile(file, "disputes/evidence");
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to upload evidence", e);
        }

        return ResponseEntity.ok(disputeService.addEvidence(disputeId, user.getId(), url));
    }

    @GetMapping("/{disputeId}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<DisputeResponse> getDisputeDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID disputeId) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(disputeService.getDisputeDetails(disputeId, user.getId()));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Quick helper to resolve profile ID. Ideally this logic is in a service.
    // For this implementation, I will inject the repo.

    private UUID getBuyerId(User user) {
        return buyerProfileRepository.findByUserId(user.getId())
                .map(com.IndiExport.backend.entity.BuyerProfile::getId)
                .orElseThrow(() -> new RuntimeException("Buyer profile not found"));
    }
}
