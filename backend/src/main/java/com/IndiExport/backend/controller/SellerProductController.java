package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.ProductDto;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.ProductMediaService;
import com.IndiExport.backend.service.ProductService;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sellers/products")
@RequiredArgsConstructor
public class SellerProductController {

    private final ProductService productService;
    private final ProductMediaService productMediaService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductDto.ProductResponse> createProduct(@Valid @RequestBody ProductDto.ProductCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(getCurrentUserId(), request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductDto.ProductResponse> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductDto.ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(getCurrentUserId(), id, request));
    }

    // LIST endpoint is handled by SellerController.getSellerProducts to support pagination
    // Removed duplicate getMyProducts method

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductDto.ProductResponse> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductDetails(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/media")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<ProductDto.ProductMediaResponse>> uploadMedia(
            @PathVariable UUID id,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        return ResponseEntity.ok(productMediaService.uploadMedia(getCurrentUserId(), id, files));
    }

    @DeleteMapping("/media/{mediaId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteMedia(@PathVariable UUID mediaId) {
        productMediaService.deleteMedia(getCurrentUserId(), mediaId);
        return ResponseEntity.noContent().build();
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
