package com.IndiExport.backend.repository.analytics;

import com.IndiExport.backend.dto.analytics.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class AnalyticsRepository {

    private final JdbcTemplate jdbcTemplate;

    // ==========================================
    // BUYER ANALYTICS
    // ==========================================
    public BuyerDashboardAnalyticsResponse getBuyerAnalytics(UUID buyerId, Instant from, Instant to) {
        Timestamp fromTs = Timestamp.from(from);
        Timestamp toTs = Timestamp.from(to);

        // 1. Basic aggregates
        String sql = """
            SELECT 
                COUNT(*) as total_orders,
                COUNT(CASE WHEN status IN ('SHIPPED', 'IN_TRANSIT') THEN 1 END) as active_shipments,
                COUNT(CASE WHEN status IN ('DELIVERED', 'COMPLETED') THEN 1 END) as completed_orders,
                COALESCE(SUM(total_amount_paise), 0) as total_spending
            FROM orders
            WHERE buyer_id = ? 
            AND created_at BETWEEN ? AND ?
            AND status != 'CANCELLED'
        """;

        Map<String, Object> stats = jdbcTemplate.queryForMap(sql, buyerId, fromTs, toTs);

        // Sub-query for buyer currency spending
        String currencySql = """
            SELECT ocs.buyer_currency, COALESCE(SUM(ocs.converted_total_minor), 0) as total_minor
            FROM order_currency_snapshot ocs
            JOIN orders o ON o.id = ocs.order_id
            WHERE o.buyer_id = ? AND o.created_at BETWEEN ? AND ? AND o.status != 'CANCELLED'
            GROUP BY ocs.buyer_currency
            ORDER BY total_minor DESC
            LIMIT 1
        """;
        
        String buyerCurrencyCode = "USD"; // Default
        Long totalSpendingMinor = 0L;
        
        try {
            Map<String, Object> currencyStats = jdbcTemplate.queryForMap(currencySql, buyerId, fromTs, toTs);
            buyerCurrencyCode = (String) currencyStats.get("buyer_currency");
            totalSpendingMinor = ((Number) currencyStats.get("total_minor")).longValue();
        } catch (Exception e) {
            // No results or error, keep defaults
        }

        // 2. Last 5 orders
        String lastOrdersSql = """
            SELECT id, order_number, status, total_amount_paise, created_at
            FROM orders
            WHERE buyer_id = ?
            ORDER BY created_at DESC
            LIMIT 5
        """;
        
        List<AnalyticsOrderSummaryDTO> lastOrders = jdbcTemplate.query(lastOrdersSql, (rs, rowNum) -> AnalyticsOrderSummaryDTO.builder()
                .orderId(UUID.fromString(rs.getString("id")))
                .orderNumber(rs.getString("order_number"))
                .status(rs.getString("status"))
                .totalAmountPaise(rs.getLong("total_amount_paise"))
                .createdAt(rs.getTimestamp("created_at").toInstant())
                .build(),
                buyerId
        );

        return BuyerDashboardAnalyticsResponse.builder()
                .totalOrders(((Number) stats.get("total_orders")).longValue())
                .activeShipmentsCount(((Number) stats.get("active_shipments")).longValue())
                .completedOrders(((Number) stats.get("completed_orders")).longValue())
                .totalSpendingINRPaise(((Number) stats.get("total_spending")).longValue())
                .totalSpendingBuyerCurrencyMinor(totalSpendingMinor)
                .buyerCurrencyCode(buyerCurrencyCode)
                .lastOrdersSummary(lastOrders)
                .build();
    }

    // ==========================================
    // SELLER ANALYTICS
    // ==========================================
    public SellerDashboardAnalyticsResponse getSellerAnalytics(UUID sellerId, Instant from, Instant to) {
        String sql = """
            SELECT 
                COUNT(*) as total_sales,
                COALESCE(SUM(total_amount_paise), 0) as total_revenue,
                COUNT(CASE WHEN status IN ('PENDING_CONFIRMATION', 'CONFIRMED', 'PAID') THEN 1 END) as pending_orders,
                COUNT(CASE WHEN status = 'SHIPPED' THEN 1 END) as shipped_orders,
                COUNT(CASE WHEN status = 'DELIVERED' THEN 1 END) as delivered_orders
            FROM orders
            WHERE seller_id = ?
            AND created_at BETWEEN ? AND ?
            AND status != 'CANCELLED'
        """;

        Map<String, Object> stats = jdbcTemplate.queryForMap(sql, sellerId, Timestamp.from(from), Timestamp.from(to));

        // Payouts (assuming logic based on order status or separate payout table if it existed)
        // For now, mapping 'COMPLETED' as released, others as holding
        String payoutSql = """
             SELECT 
                COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as released,
                COUNT(CASE WHEN status IN ('PAID', 'SHIPPED', 'DELIVERED') THEN 1 END) as holding
             FROM orders
             WHERE seller_id = ? AND created_at BETWEEN ? AND ?
        """;
        Map<String, Object> payoutStats = jdbcTemplate.queryForMap(payoutSql, sellerId, Timestamp.from(from), Timestamp.from(to));

        return SellerDashboardAnalyticsResponse.builder()
                .totalSalesCount(((Number) stats.get("total_sales")).longValue())
                .totalRevenueINRPaise(((Number) stats.get("total_revenue")).longValue())
                .pendingOrdersCount(((Number) stats.get("pending_orders")).longValue())
                .shippedOrdersCount(((Number) stats.get("shipped_orders")).longValue())
                .deliveredOrdersCount(((Number) stats.get("delivered_orders")).longValue())
                .payoutHoldingCount(((Number) payoutStats.get("holding")).longValue())
                .payoutReleasedCount(((Number) payoutStats.get("released")).longValue())
                .build();
    }

    // ==========================================
    // ADMIN ANALYTICS
    // ==========================================
    public AdminDashboardAnalyticsResponse getAdminAnalytics(Instant from, Instant to) {
        String sql = """
            SELECT 
                COUNT(*) as total_orders,
                COALESCE(SUM(total_amount_paise), 0) as gmv
            FROM orders
            WHERE created_at BETWEEN ? AND ?
            AND status != 'CANCELLED'
        """;
        Map<String, Object> orderStats = jdbcTemplate.queryForMap(sql, Timestamp.from(from), Timestamp.from(to));

        String userSql = """
             SELECT 
                COUNT(DISTINCT CASE WHEN r.name = 'ROLE_SELLER' THEN u.id END) as new_sellers,
                COUNT(DISTINCT CASE WHEN r.name = 'ROLE_BUYER' THEN u.id END) as new_buyers
             FROM users u
             JOIN user_roles ur ON u.id = ur.user_id
             JOIN roles r ON ur.role_id = r.id
             WHERE u.created_at BETWEEN ? AND ?
        """;
        Map<String, Object> userStats = jdbcTemplate.queryForMap(userSql, Timestamp.from(from), Timestamp.from(to));

         String disputeSql = """
             SELECT COUNT(*) FROM disputes 
             WHERE created_at BETWEEN ? AND ? AND status = 'OPEN'
        """;
        Long disputesOpen = jdbcTemplate.queryForObject(disputeSql, Long.class, Timestamp.from(from), Timestamp.from(to));

        // Top Countries
        String countrySql = """
             SELECT buyer_country, COUNT(*) as count, COALESCE(SUM(total_amount_paise), 0) as revenue
             FROM orders
             WHERE created_at BETWEEN ? AND ? AND status != 'CANCELLED'
             GROUP BY buyer_country
             ORDER BY count DESC
             LIMIT 5
        """;
        
        List<CountrySalesResponse> topCountries = jdbcTemplate.query(countrySql, (rs, rowNum) -> CountrySalesResponse.builder()
                .country(rs.getString("buyer_country"))
                .orders(rs.getLong("count"))
                .revenueINRPaise(rs.getLong("revenue"))
                .build(),
                Timestamp.from(from), Timestamp.from(to)
        );

        long gmv = ((Number) orderStats.get("gmv")).longValue();
        return AdminDashboardAnalyticsResponse.builder()
                .platformOrdersCount(((Number) orderStats.get("total_orders")).longValue())
                .platformGMVINRPaise(gmv)
                .platformCommissionINRPaise(gmv / 10) // 10% flat assumption for now
                .disputesOpenCount(disputesOpen != null ? disputesOpen : 0)
                .newSellersCount(((Number) userStats.get("new_sellers")).longValue())
                .newBuyersCount(((Number) userStats.get("new_buyers")).longValue())
                .topCountriesByOrders(topCountries)
                .build();
    }

    // ==========================================
    // ADVANCED SELLER ANALYTICS
    // ==========================================
    public AdvancedSellerAnalyticsResponse getAdvancedSellerAnalytics(UUID sellerId, Instant from, Instant to) {
        Timestamp fromTs = Timestamp.from(from);
        Timestamp toTs = Timestamp.from(to);

        // 1. Monthly Revenue Chart
        String chartSql = """
            SELECT TO_CHAR(created_at, 'YYYY-MM') as period, COALESCE(SUM(total_amount_paise), 0) as revenue
            FROM orders
            WHERE seller_id = ? AND created_at BETWEEN ? AND ? AND status != 'CANCELLED'
            GROUP BY period
            ORDER BY period ASC
        """;
        List<ChartPointResponse> revenueChart = jdbcTemplate.query(chartSql, (rs, rowNum) -> ChartPointResponse.builder()
                .period(rs.getString("period"))
                .value(rs.getLong("revenue"))
                .build(),
                sellerId, fromTs, toTs
        );

        // 2. Sales by Country
        String countrySql = """
             SELECT buyer_country, COUNT(*) as count, COALESCE(SUM(total_amount_paise), 0) as revenue
             FROM orders
             WHERE seller_id = ? AND created_at BETWEEN ? AND ? AND status != 'CANCELLED'
             GROUP BY buyer_country
             ORDER BY revenue DESC
        """;
        List<CountrySalesResponse> salesByCountry = jdbcTemplate.query(countrySql, (rs, rowNum) -> CountrySalesResponse.builder()
                .country(rs.getString("buyer_country"))
                .orders(rs.getLong("count"))
                .revenueINRPaise(rs.getLong("revenue"))
                .build(),
                sellerId, fromTs, toTs
        );

        // 3. Avg Order Value
        String avgSql = """
            SELECT COALESCE(AVG(total_amount_paise), 0) 
            FROM orders
            WHERE seller_id = ? AND created_at BETWEEN ? AND ? AND status != 'CANCELLED'
        """;
        Long avgOrderValue = jdbcTemplate.queryForObject(avgSql, Long.class, sellerId, fromTs, toTs);

        // 4. Top Products
        String productsSql = """
             SELECT oi.product_id, oi.product_name_snapshot, COUNT(*) as count, COALESCE(SUM(oi.line_total_paise), 0) as revenue
             FROM order_items oi
             JOIN orders o ON o.id = oi.order_id
             WHERE o.seller_id = ? AND o.created_at BETWEEN ? AND ? AND o.status != 'CANCELLED'
             GROUP BY oi.product_id, oi.product_name_snapshot
             ORDER BY revenue DESC
             LIMIT 5
        """;
        List<ProductSalesResponse> topProducts = jdbcTemplate.query(productsSql, (rs, rowNum) -> ProductSalesResponse.builder()
                .productId(UUID.fromString(rs.getString("product_id")))
                .title(rs.getString("product_name_snapshot"))
                .orders(rs.getLong("count"))
                .revenueINRPaise(rs.getLong("revenue"))
                .build(),
                sellerId, fromTs, toTs
        );
        
        // 5. Product Views (Conversion)
        String viewsSql = "SELECT COUNT(*) FROM product_views pv JOIN products p ON p.id = pv.product_id WHERE p.seller_id = ? AND pv.viewed_at BETWEEN ? AND ?";
        Long totalViews = jdbcTemplate.queryForObject(viewsSql, Long.class, sellerId, fromTs, toTs);
        
        String ordersSql = "SELECT COUNT(*) FROM orders WHERE seller_id = ? AND created_at BETWEEN ? AND ? AND status != 'CANCELLED'";
        Long totalOrders = jdbcTemplate.queryForObject(ordersSql, Long.class, sellerId, fromTs, toTs);
        
        Double conversionRate = (totalViews != null && totalViews > 0) ? (double) totalOrders / totalViews * 100.0 : 0.0;
        
        return AdvancedSellerAnalyticsResponse.builder()
                .monthlyRevenueChart(revenueChart)
                .salesByCountry(salesByCountry)
                .avgOrderValueINRPaise(avgOrderValue != null ? avgOrderValue : 0)
                .topProducts(topProducts)
                .rfqSuccessRate(0.0) // Placeholder
                .totalProductViews(totalViews != null ? totalViews : 0)
                .totalOrdersFromViews(totalOrders) // A simpler proxy for now
                .globalConversionRate(conversionRate)
                .build();
    }
}
