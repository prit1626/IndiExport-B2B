package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.ProductDto.ProductFilterRequest;
import com.IndiExport.backend.dto.ProductDto.BuyerProductCardResponse;
import com.IndiExport.backend.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/search")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @GetMapping
    public ResponseEntity<Page<BuyerProductCardResponse>> search(
            @ModelAttribute ProductFilterRequest filter,
            @RequestParam(required = false) String currency) {
        return ResponseEntity.ok(productSearchService.searchProducts(filter, currency));
    }
}
