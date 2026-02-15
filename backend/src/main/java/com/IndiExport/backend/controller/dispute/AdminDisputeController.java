package com.IndiExport.backend.controller.dispute;

import com.IndiExport.backend.dto.dispute.AdminDisputeResponse;
import com.IndiExport.backend.dto.dispute.AdminResolveDisputeRequest;
import com.IndiExport.backend.dto.dispute.DisputeResponse;
import com.IndiExport.backend.entity.Dispute;
import com.IndiExport.backend.entity.DisputeStatus;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.dispute.DisputeResolutionService;
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
@RequestMapping("/api/v1/admin/disputes")
@RequiredArgsConstructor
public class AdminDisputeController {

    private final DisputeService disputeService;
    private final DisputeResolutionService disputeResolutionService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdminDisputeResponse>> getAllDisputes(
            @RequestParam(required = false) DisputeStatus status,
            @RequestParam(required = false) UUID buyerId,
            @RequestParam(required = false) UUID sellerId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        return ResponseEntity.ok(disputeService.getAllDisputesAdmin(status, buyerId, sellerId, pageable));
    }
    
    @GetMapping("/{disputeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DisputeResponse> getDisputeDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID disputeId) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(disputeService.getDisputeDetails(disputeId, user.getId()));
    }

    @PutMapping("/{disputeId}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Dispute> resolveDispute(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID disputeId,
            @RequestBody @Valid AdminResolveDisputeRequest request) {
        
        User user = getUser(userDetails);
        return ResponseEntity.ok(disputeResolutionService.resolveDispute(disputeId, user.getId(), request));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
