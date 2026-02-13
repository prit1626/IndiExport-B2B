package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.SellerKycDto;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.AdminVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/sellers")
@RequiredArgsConstructor
public class AdminSellerVerificationController {

    private final AdminVerificationService adminVerificationService;

    @GetMapping("/pending-verification")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SellerKycDto.AdminSellerKycResponse>> getPendingSellers() {
        return ResponseEntity.ok(adminVerificationService.getPendingSellers());
    }

    @GetMapping("/{sellerId}/kyc")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SellerKycDto.AdminSellerKycResponse> getSellerKyc(@PathVariable UUID sellerId) {
        return ResponseEntity.ok(adminVerificationService.getSellerKyc(sellerId));
    }

    @PutMapping("/{sellerId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> verifySeller(@PathVariable UUID sellerId) {
        adminVerificationService.approveSeller(getCurrentUserId(), sellerId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{sellerId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectSeller(@PathVariable UUID sellerId, @Valid @RequestBody SellerKycDto.AdminRejectSellerRequest request) {
        adminVerificationService.rejectSeller(getCurrentUserId(), sellerId, request);
        return ResponseEntity.ok().build();
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object details = auth.getDetails();
        if (details instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            return UUID.fromString(((JwtAuthenticationFilter.JwtAuthenticationDetails) details).getUserId());
        }
        throw new IllegalStateException("User not authenticated");
    }
}
