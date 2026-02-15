package com.IndiExport.backend.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class AdminSettingsResponse {
    private UUID id;
    private long advancedSellerPlanPriceInrPaise;
    private int platformCommissionBps;
    private int disputeWindowDays;
    private int autoReleaseDays;
    private int basicSellerMaxActiveProducts;
    private Instant updatedAt;
}
