package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.SellerKycDto;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.SellerKycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/seller/kyc")
@RequiredArgsConstructor
public class SellerKycController {

    private final SellerKycService sellerKycService;

    @GetMapping("/status")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerKycDto.SellerKycStatusResponse> getKycStatus() {
        return ResponseEntity.ok(sellerKycService.getKycStatus(getCurrentUserId()));
    }

    @PostMapping("/upload-iec")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, String>> uploadIec(
            @RequestParam("iecNumber") String iecNumber,
            @RequestParam("file") MultipartFile file) throws IOException {
        String url = sellerKycService.uploadIec(getCurrentUserId(), iecNumber, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("url", url));
    }

    @PostMapping("/upload-pan")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, String>> uploadPan(
            @RequestParam("panNumber") String panNumber,
            @RequestParam("file") MultipartFile file) throws IOException {
        String url = sellerKycService.uploadPan(getCurrentUserId(), panNumber, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("url", url));
    }

    @PostMapping("/upload-gstin")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, String>> uploadGstin(
            @RequestParam(value = "gstinNumber", required = false) String gstinNumber,
            @RequestParam("file") MultipartFile file) throws IOException {
        String url = sellerKycService.uploadGstin(getCurrentUserId(), gstinNumber, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("url", url));
    }

    @PostMapping("/upload-bank-details")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> uploadBankDetails(@Valid @RequestBody SellerKycDto.UpdateBankDetailsRequest request) {
        sellerKycService.updateBankDetails(getCurrentUserId(), request);
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
