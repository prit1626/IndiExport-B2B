package com.IndiExport.backend.controller.admin;

import com.IndiExport.backend.dto.admin.TermsResponse;
import com.IndiExport.backend.dto.admin.UpdateTermsRequest;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.admin.TermsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/terms")
@RequiredArgsConstructor
public class AdminTermsController {

    private final TermsService termsService;
    private final UserRepository userRepository;

    @PostMapping("/versions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TermsResponse> createVersion(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UpdateTermsRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(termsService.createNewVersion(user.getId(), request));
    }

    @PutMapping("/versions/{termsId}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TermsResponse> publishVersion(@PathVariable UUID termsId) {
        return ResponseEntity.ok(termsService.publishVersion(termsId));
    }
    
    // Add endpoint to list versions if needed, skipping for MVP strictness unless requested.

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
