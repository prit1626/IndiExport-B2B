package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.CartDto;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Buyer cart endpoints.
 * All endpoints require BUYER role.
 */
@RestController
@RequestMapping("/api/v1/buyer/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class BuyerCartController {

    private final CartService cartService;

    /**
     * POST /api/v1/buyer/cart/add
     * Add a product to cart (or increment if already exists).
     */
    @PostMapping("/add")
    public ResponseEntity<CartDto.CartItemResponse> addToCart(
            @Valid @RequestBody CartDto.CartAddRequest request) {
        UUID userId = getCurrentUserId();
        CartDto.CartItemResponse response = cartService.addToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/v1/buyer/cart/item/{id}
     * Update quantity and/or shipping mode.
     */
    @PutMapping("/item/{id}")
    public ResponseEntity<CartDto.CartItemResponse> updateCartItem(
            @PathVariable UUID id,
            @Valid @RequestBody CartDto.CartUpdateRequest request) {
        UUID userId = getCurrentUserId();
        CartDto.CartItemResponse response = cartService.updateCartItem(userId, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/buyer/cart/item/{id}
     * Remove an item from cart.
     */
    @DeleteMapping("/item/{id}")
    public ResponseEntity<Void> removeCartItem(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        cartService.removeCartItem(userId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/buyer/cart
     * Get full cart with items and totals.
     */
    @GetMapping
    public ResponseEntity<CartDto.CartResponse> getCart() {
        UUID userId = getCurrentUserId();
        CartDto.CartResponse response = cartService.getCart(userId);
        return ResponseEntity.ok(response);
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object details = auth.getDetails();
        if (details instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            return UUID.fromString(
                    ((JwtAuthenticationFilter.JwtAuthenticationDetails) details).getUserId());
        }
        throw new IllegalStateException("User not authenticated");
    }
}
