package com.IndiExport.backend.controller;

import com.IndiExport.backend.service.ProductSuggestionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products/suggestions")
@RequiredArgsConstructor
public class ProductSuggestionsController {

    private final ProductSuggestionsService productSuggestionsService;

    @GetMapping
    public ResponseEntity<List<String>> getSuggestions(@RequestParam String keyword) {
        return ResponseEntity.ok(productSuggestionsService.getSuggestions(keyword));
    }
}
