package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.ProductDto;
import com.IndiExport.backend.service.ProductSearchService;
import com.IndiExport.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class PublicProductController {

    private final ProductSearchService productSearchService;
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDto.BuyerProductCardResponse>> searchProducts(
            @ModelAttribute ProductDto.ProductFilterRequest filter,
            @RequestParam(required = false) String currency) {
        return ResponseEntity.ok(productSearchService.searchProducts(filter, currency));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto.ProductResponse> getProductDetails(
            @PathVariable UUID id,
            @RequestParam(required = false) String currency) {
        return ResponseEntity.ok(productService.getProductDetails(id, currency));
    }
}

