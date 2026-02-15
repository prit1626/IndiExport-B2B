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
public class AdminDashboardAnalyticsResponse {
    private Long platformOrdersCount;
    private Long platformGMVINRPaise;
    private Long platformCommissionINRPaise;
    private Long disputesOpenCount;
    private Long newSellersCount;
    private Long newBuyersCount;
    private List<CountrySalesResponse> topCountriesByOrders;
}
