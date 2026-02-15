package com.IndiExport.backend.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerDashboardAnalyticsResponse {
    private Long totalSalesCount;
    private Long totalRevenueINRPaise;
    private Long pendingOrdersCount;
    private Long shippedOrdersCount;
    private Long deliveredOrdersCount;
    private Long payoutHoldingCount;
    private Long payoutReleasedCount;
}
