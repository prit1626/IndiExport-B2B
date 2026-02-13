package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.ShippingTemplateDto;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.SellerShippingTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seller/shipping/templates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerShippingTemplateController {

    private final SellerShippingTemplateService templateService;

    @PostMapping
    public ResponseEntity<ShippingTemplateDto.Response> create(
            @Valid @RequestBody ShippingTemplateDto.CreateRequest request) {
        UUID userId = getCurrentUserId();
        ShippingTemplateDto.Response response = templateService.createTemplate(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShippingTemplateDto.Response> update(
            @PathVariable UUID id,
            @Valid @RequestBody ShippingTemplateDto.UpdateRequest request) {
        UUID userId = getCurrentUserId();
        ShippingTemplateDto.Response response = templateService.updateTemplate(userId, id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ShippingTemplateDto.Response>> getAll() {
        UUID userId = getCurrentUserId();
        List<ShippingTemplateDto.Response> templates = templateService.getTemplates(userId);
        return ResponseEntity.ok(templates);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        templateService.deleteTemplate(userId, id);
        return ResponseEntity.noContent().build();
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
