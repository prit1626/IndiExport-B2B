package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.ShippingQuoteDto;
import com.IndiExport.backend.service.ShippingQuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public shipping quote endpoint.
 * Accessible without authentication so buyers can estimate costs pre-login.
 */
@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingQuoteService shippingQuoteService;

    /**
     * POST /api/v1/shipping/quote
     * Calculate a shipping cost estimate based on items, destination, and mode.
     */
    @PostMapping("/quote")
    public ResponseEntity<ShippingQuoteDto.QuoteResponse> getQuote(
            @Valid @RequestBody ShippingQuoteDto.QuoteRequest request) {
        ShippingQuoteDto.QuoteResponse quote = shippingQuoteService.calculateQuote(request);
        return ResponseEntity.ok(quote);
    }
}
