package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.BuyerRfqResponse;
import com.IndiExport.backend.dto.SellerRfqListResponse;
import com.IndiExport.backend.entity.Incoterm;
import com.IndiExport.backend.entity.ShippingMode;
import com.IndiExport.backend.service.rfq.RfqService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seller/rfq")
@RequiredArgsConstructor
public class SellerRfqController {

    private final RfqService rfqService;
    private final UserRepository userRepository;

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object details = auth.getDetails();
        if (details instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            return UUID.fromString(((JwtAuthenticationFilter.JwtAuthenticationDetails) details).getUserId());
        }
        return userRepository.findByEmail(auth.getName()).orElseThrow().getId();
    }

    @GetMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<SellerRfqListResponse>> searchRfqs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String destinationCountry,
            @RequestParam(required = false) Integer minQty,
            @RequestParam(required = false) Integer maxQty,
            @RequestParam(required = false) ShippingMode shippingMode,
            @RequestParam(required = false) Incoterm incoterm,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(rfqService.searchRfqs(
                keyword, categoryId, destinationCountry, minQty, maxQty, shippingMode, incoterm, pageable));
    }

    @GetMapping("/{rfqId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<BuyerRfqResponse> getRfqById(@PathVariable UUID rfqId) {
        return ResponseEntity.ok(rfqService.getRfqForSeller(rfqId));
    }

    @GetMapping("/recommended")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<SellerRfqListResponse>> getRecommendedRfqs(
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(rfqService.getRecommendedRfqs(getCurrentUserId(), pageable));
    }
}
