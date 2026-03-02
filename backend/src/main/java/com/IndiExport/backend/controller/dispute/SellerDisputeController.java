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
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/v1/seller/disputes")
public class SellerDisputeController {

    private final DisputeService disputeService;
    private final UserRepository userRepository;
    private final com.IndiExport.backend.repository.SellerProfileRepository sellerProfileRepository;
    private final com.IndiExport.backend.service.FileStorageService fileStorageService;

    @Autowired
    public SellerDisputeController(DisputeService disputeService, UserRepository userRepository, com.IndiExport.backend.repository.SellerProfileRepository sellerProfileRepository, com.IndiExport.backend.service.FileStorageService fileStorageService) {
        this.disputeService = disputeService;
        this.userRepository = userRepository;
        this.sellerProfileRepository = sellerProfileRepository;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<DisputeResponse> raiseDispute(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid RaiseDisputeRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(disputeService.raiseDispute(user.getId(), request));
    }

    @GetMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<DisputeListResponse>> getMyDisputes(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) com.IndiExport.backend.entity.DisputeStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(disputeService.getDisputesForSeller(getSellerId(user), status, pageable));
    }

    @PostMapping(value = "/{disputeId}/evidence", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('SELLER')")
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
    @PreAuthorize("hasRole('SELLER')")
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
    
    private UUID getSellerId(User user) {
        return sellerProfileRepository.findByUserId(user.getId())
                .map(com.IndiExport.backend.entity.SellerProfile::getId)
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));
    }
}
