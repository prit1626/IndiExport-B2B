package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.BuyerProfileDto;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.BuyerProfileService;
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
@RequestMapping("/api/v1/buyer/profile")
@RequiredArgsConstructor
public class BuyerProfileController {

    private final BuyerProfileService buyerProfileService;

    @GetMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<BuyerProfileDto.BuyerProfileResponse> getProfile() {
        return ResponseEntity.ok(buyerProfileService.getProfile(getCurrentUserId()));
    }

    @PutMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<BuyerProfileDto.BuyerProfileResponse> updateProfile(@Valid @RequestBody BuyerProfileDto.UpdateBuyerProfileRequest request) {
        return ResponseEntity.ok(buyerProfileService.updateProfile(getCurrentUserId(), request));
    }

    @PostMapping("/photo")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Map<String, String>> uploadPhoto(@RequestParam("file") MultipartFile file) throws IOException {
        String url = buyerProfileService.uploadPhoto(getCurrentUserId(), file);
        return ResponseEntity.ok(Collections.singletonMap("profilePhotoUrl", url));
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
