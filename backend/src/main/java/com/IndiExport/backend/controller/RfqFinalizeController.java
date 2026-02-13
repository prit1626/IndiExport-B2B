package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.RfqFinalizeRequest;
import com.IndiExport.backend.dto.RfqFinalizeResponse;
import com.IndiExport.backend.entity.BuyerProfile;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.BuyerProfileRepository;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.rfq.RfqFinalizeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rfq")
@RequiredArgsConstructor
public class RfqFinalizeController {

    private final RfqFinalizeService rfqFinalizeService;
    private final UserRepository userRepository;
    private final BuyerProfileRepository buyerProfileRepository;

    @PostMapping("/{rfqId}/finalize")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<RfqFinalizeResponse> finalizeRfq(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID rfqId,
            @Valid @RequestBody RfqFinalizeRequest request) {
        
        BuyerProfile buyer = resolveBuyer(userDetails);
        RfqFinalizeResponse response = rfqFinalizeService.finalizeRfq(buyer.getId(), rfqId, request.getQuoteId());
        return ResponseEntity.ok(response);
    }

    private BuyerProfile resolveBuyer(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        return buyerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer profile not found for user"));
    }
}
