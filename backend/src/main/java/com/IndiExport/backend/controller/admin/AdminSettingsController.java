package com.IndiExport.backend.controller.admin;

import com.IndiExport.backend.dto.admin.AdminSettingsResponse;
import com.IndiExport.backend.dto.admin.UpdateAdminSettingsRequest;
import com.IndiExport.backend.service.admin.AdminSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final AdminSettingsService settingsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminSettingsResponse> getSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminSettingsResponse> updateSettings(@RequestBody @Valid UpdateAdminSettingsRequest request) {
        return ResponseEntity.ok(settingsService.updateSettings(request));
    }
}
