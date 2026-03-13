package com.IndiExport.backend.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerDashboardAnalyticsResponse {
    private Long totalOrders;
    private Long activeShipmentsCount;
    private Long completedOrders;
    private Long totalSpending;
    private List<ChartPointResponse> ordersOverTime;
    private List<ChartPointResponse> spendingOverTime;
    private List<AnalyticsOrderSummaryDTO> lastOrders;
    private String preferredCurrency;
}
