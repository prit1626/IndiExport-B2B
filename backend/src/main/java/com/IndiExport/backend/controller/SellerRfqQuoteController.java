package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.SellerQuoteCreateRequest;
import com.IndiExport.backend.dto.SellerQuoteResponse;
import com.IndiExport.backend.entity.SellerProfile;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.SellerProfileRepository;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.rfq.RfqQuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seller/rfq")
@RequiredArgsConstructor
public class SellerRfqQuoteController {

    private final RfqQuoteService rfqQuoteService;
    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;

    @PostMapping("/{rfqId}/quote")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerQuoteResponse> submitQuote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID rfqId,
            @Valid @RequestBody SellerQuoteCreateRequest request) {
        
        SellerProfile seller = resolveSeller(userDetails);
        SellerQuoteResponse response = rfqQuoteService.submitQuote(seller.getId(), rfqId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private SellerProfile resolveSeller(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        return sellerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found for user"));
    }
}
