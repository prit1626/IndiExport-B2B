package com.IndiExport.backend.dto.admin;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAdminSettingsRequest {

    @Min(value = 0, message = "Price cannot be negative")
    private Long advancedSellerPlanPriceInrPaise;

    @Min(value = 0, message = "Commission cannot be negative")
    private Integer platformCommissionBps;

    @Min(value = 1, message = "Dispute window must be at least 1 day")
    private Integer disputeWindowDays;

    @Min(value = 1, message = "Auto release must be at least 1 day")
    private Integer autoReleaseDays;

    @Min(value = 1, message = "Basic seller max products must be at least 1")
    private Integer basicSellerMaxActiveProducts;
}
