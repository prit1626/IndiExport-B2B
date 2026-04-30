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
public class AdvancedSellerAnalyticsResponse {
    private List<ChartPointResponse> monthlyRevenueChart;
    private List<CountrySalesResponse> salesByCountry;
    private Long avgOrderValueINRPaise;
    private List<ProductSalesResponse> topProducts;
    private Double rfqSuccessRate; // percentage
    
    // Conversion analytics
    private Long totalProductViews;
    private Long totalOrdersFromViews; // Approximation
    private Double globalConversionRate;

    // Advanced Metrics
    private PeriodicStatsResponse viewStats;
    private PeriodicStatsResponse inquiryStats;
    private List<ProductPerformanceResponse> topProductsByViews;
    private List<ProductPerformanceResponse> topProductsByInquiries;
    private RfqOpportunityStatsResponse rfqOpportunityStats;
    private List<CountrySalesResponse> topBuyerCountries;
    private List<RecentActivityResponse> recentActivities;
}
