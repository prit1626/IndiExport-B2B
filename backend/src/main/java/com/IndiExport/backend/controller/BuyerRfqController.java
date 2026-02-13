package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.BuyerRfqCreateRequest;
import com.IndiExport.backend.dto.BuyerRfqResponse;
import com.IndiExport.backend.entity.BuyerProfile;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.BuyerProfileRepository;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.rfq.RfqService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buyer/rfq")
@RequiredArgsConstructor
public class BuyerRfqController {

    private final RfqService rfqService;
    private final UserRepository userRepository;
    private final BuyerProfileRepository buyerProfileRepository;

    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<BuyerRfqResponse> createRfq(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BuyerRfqCreateRequest request) {
        
        BuyerProfile buyer = resolveBuyer(userDetails);
        BuyerRfqResponse response = rfqService.createRfq(buyer.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Page<BuyerRfqResponse>> getMyRfqs(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        BuyerProfile buyer = resolveBuyer(userDetails);
        return ResponseEntity.ok(rfqService.getBuyerRfqs(buyer.getId(), pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<BuyerRfqResponse> getRfq(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id) {
        
        BuyerProfile buyer = resolveBuyer(userDetails);
        return ResponseEntity.ok(rfqService.getRfqForBuyer(buyer.getId(), id));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Void> cancelRfq(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id) {
        
        BuyerProfile buyer = resolveBuyer(userDetails);
        rfqService.cancelRfq(buyer.getId(), id);
        return ResponseEntity.noContent().build();
    }

    private BuyerProfile resolveBuyer(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        return buyerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer profile not found for user"));
    }
}
