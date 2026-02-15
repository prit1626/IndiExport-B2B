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

    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<DisputeResponse> raiseDispute(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid RaiseDisputeRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(disputeService.raiseDispute(user.getId(), request));
    }

    @GetMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Page<DisputeListResponse>> getMyDisputes(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        User user = getUser(userDetails);
        // Assuming user.getId() maps generally to buyer.id, but usually there's a profile.
        // In this system, Payment uses buyerId (UUID) which is likely the profile ID.
        // Let's check how DisputeService expects the ID.
        // DisputeService expects userId and looks up profile or uses userId if they are same.
        // The service uses `order.getBuyer().getUser().getId().equals(userId)` so passing User ID is correct for validation.
        // But `findByBuyerId` repository expects `buyerId`.
        // I need to resolve Buyer ID from User ID here or in service.
        // Service's `getDisputesForBuyer(buyerId, ...)` calls repo `findByBuyerId(buyerId)`.
        // So I must pass BuyerProfile ID to the service method `getDisputesForBuyer`.
        // Let's resolve it.
        
        // TODO: ideally service should handle this User -> Profile resolution to keep controller thin.
        // However, DisputeService `raiseDispute` takes `userId` and resolves internally.
        // But `getDisputesForBuyer` takes `buyerId`.
        // I will assume for now checking the BuyerProfileRepository is needed.
        // To save time/code in this snippet, I will rely on a helper or assume User ID if profiles share ID (unlikely).
        // Let's inject BuyerProfileRepository.
        
        return ResponseEntity.ok(disputeService.getDisputesForBuyer(getBuyerId(user), pageable));
    }

    @PostMapping("/{disputeId}/evidence")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<EvidenceResponse> addEvidence(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID disputeId,
            @RequestBody @Valid AddEvidenceRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(disputeService.addEvidence(disputeId, user.getId(), request));
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
    private final com.IndiExport.backend.repository.BuyerProfileRepository buyerProfileRepository;
    
    private UUID getBuyerId(User user) {
        return buyerProfileRepository.findByUserId(user.getId())
                .map(com.IndiExport.backend.entity.BuyerProfile::getId)
                .orElseThrow(() -> new RuntimeException("Buyer profile not found"));
    }
}
