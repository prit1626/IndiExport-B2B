package com.IndiExport.backend.controller.common;

import com.IndiExport.backend.dto.admin.AcceptTermsRequest;
import com.IndiExport.backend.dto.admin.AcceptanceResponse;
import com.IndiExport.backend.dto.admin.TermsResponse;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.admin.TermsAcceptanceService;
import com.IndiExport.backend.service.admin.TermsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/terms")
@RequiredArgsConstructor
public class TermsController {

    private final TermsService termsService;
    private final TermsAcceptanceService acceptanceService;
    private final UserRepository userRepository;

    @GetMapping("/latest")
    public ResponseEntity<TermsResponse> getLatestTerms() {
        return ResponseEntity.ok(termsService.getLatestPublishedTerms());
    }

    @PostMapping("/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AcceptanceResponse> acceptTerms(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid AcceptTermsRequest request,
            HttpServletRequest servletRequest) {
        
        User user = getUser(userDetails);
        String ipAddress = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");
        
        return ResponseEntity.ok(acceptanceService.acceptTerms(user.getId(), request.getTermsVersionId(), ipAddress, userAgent));
    }

    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> hasAcceptedLatest(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(acceptanceService.hasAcceptedLatest(user.getId()));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
