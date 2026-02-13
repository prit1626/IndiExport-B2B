package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.InvoiceDownloadResponse;
import com.IndiExport.backend.dto.InvoiceListResponse;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.BuyerProfileRepository;
import com.IndiExport.backend.repository.SellerProfileRepository;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.invoice.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final BuyerProfileRepository buyerProfileRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final UserRepository userRepository;

    @GetMapping("/{id}/download")
    public ResponseEntity<InvoiceDownloadResponse> downloadInvoice(@PathVariable UUID id) {
        UUID currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();
        
        // Service handles access control logic
        InvoiceDownloadResponse response = invoiceService.downloadInvoice(id, currentUserId, isAdmin);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buyer")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Page<InvoiceListResponse>> listBuyerInvoices(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        UUID currentUserId = getCurrentUserId();
        // Access repository directly or move this logic to service if complex.
        // For now, controller orchestration is acceptable.
        UUID buyerProfileId = buyerProfileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer profile not found"))
                .getId();
        
        return ResponseEntity.ok(invoiceService.listInvoicesForBuyer(buyerProfileId, pageable));
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<InvoiceListResponse>> listSellerInvoices(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        UUID currentUserId = getCurrentUserId();
        UUID sellerProfileId = sellerProfileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"))
                .getId();
        
        return ResponseEntity.ok(invoiceService.listInvoicesForSeller(sellerProfileId, pageable));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InvoiceListResponse>> listAllInvoices(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(invoiceService.listAllInvoices(pageable));
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getId();
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
             String email = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
             return userRepository.findByEmail(email)
                     .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                     .getId();
        }
        // Fallback for JWT string principal if applicable
        if (principal instanceof String) {
            // Assume string is email if it looks like one, or username
             return userRepository.findByEmail((String) principal)
                     .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                     .getId();
        }
        
        throw new RuntimeException("Unknown principal type: " + principal.getClass());
    }
    
    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
