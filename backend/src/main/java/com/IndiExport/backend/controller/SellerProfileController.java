package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.SellerProfileDto;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.SellerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seller/profile")
@RequiredArgsConstructor
public class SellerProfileController {

    private final SellerProfileService sellerProfileService;

    @GetMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerProfileDto.SellerProfileResponse> getProfile() {
        return ResponseEntity.ok(sellerProfileService.getProfile(getCurrentUserId()));
    }

    @PutMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerProfileDto.SellerProfileResponse> updateProfile(@Valid @RequestBody SellerProfileDto.UpdateSellerProfileRequest request) {
        return ResponseEntity.ok(sellerProfileService.updateProfile(getCurrentUserId(), request));
    }

    @PostMapping("/logo")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, String>> uploadLogo(@RequestParam("file") MultipartFile file) throws IOException {
        String url = sellerProfileService.uploadLogo(getCurrentUserId(), file);
        return ResponseEntity.ok(Collections.singletonMap("logoUrl", url));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<SellerProfileDto.SellerProfileResponse> getPublicProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(sellerProfileService.getPublicProfile(id));
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
