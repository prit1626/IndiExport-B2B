package com.IndiExport.backend.service.admin;

import com.IndiExport.backend.dto.admin.AdminSettingsResponse;
import com.IndiExport.backend.dto.admin.UpdateAdminSettingsRequest;
import com.IndiExport.backend.entity.AdminSettings;
import com.IndiExport.backend.repository.AdminSettingsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminSettingsService {

    private final AdminSettingsRepository settingsRepository;

    @PostConstruct
    public void init() {
        if (settingsRepository.count() == 0) {
            log.info("Initializing default admin settings...");
            settingsRepository.save(new AdminSettings());
        }
    }

    @Transactional(readOnly = true)
    public AdminSettingsResponse getSettings() {
        AdminSettings settings = getSettingsEntity();
        return mapToResponse(settings);
    }
    
    @Transactional(readOnly = true)
    public AdminSettings getSettingsEntity() {
        return settingsRepository.findFirst()
                .orElseThrow(() -> new RuntimeException("Admin settings not initialized"));
    }

    @Transactional
    public AdminSettingsResponse updateSettings(UpdateAdminSettingsRequest request) {
        AdminSettings settings = getSettingsEntity();

        if (request.getAdvancedSellerPlanPriceInrPaise() != null) {
            settings.setAdvancedSellerPlanPriceInrPaise(request.getAdvancedSellerPlanPriceInrPaise());
        }
        if (request.getPlatformCommissionBps() != null) {
            settings.setPlatformCommissionBps(request.getPlatformCommissionBps());
        }
        if (request.getDisputeWindowDays() != null) {
            settings.setDisputeWindowDays(request.getDisputeWindowDays());
        }
        if (request.getAutoReleaseDays() != null) {
            settings.setAutoReleaseDays(request.getAutoReleaseDays());
        }
        if (request.getBasicSellerMaxActiveProducts() != null) {
            settings.setBasicSellerMaxActiveProducts(request.getBasicSellerMaxActiveProducts());
        }

        return mapToResponse(settingsRepository.save(settings));
    }

    private AdminSettingsResponse mapToResponse(AdminSettings settings) {
        return AdminSettingsResponse.builder()
                .id(settings.getId())
                .advancedSellerPlanPriceInrPaise(settings.getAdvancedSellerPlanPriceInrPaise())
                .platformCommissionBps(settings.getPlatformCommissionBps())
                .disputeWindowDays(settings.getDisputeWindowDays())
                .autoReleaseDays(settings.getAutoReleaseDays())
                .basicSellerMaxActiveProducts(settings.getBasicSellerMaxActiveProducts())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}
