package com.IndiExport.backend.repository.analytics;

import com.IndiExport.backend.dto.analytics.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class AnalyticsRepository {

    private final JdbcTemplate jdbcTemplate;

    // ==========================================
    // BUYER ANALYTICS
    // ==========================================
    public BuyerDashboardAnalyticsResponse getBuyerAnalytics(UUID buyerId, Instant from, Instant to) {
        LocalDateTime fromLdt = LocalDateTime.ofInstant(from, ZoneId.systemDefault());
        LocalDateTime toLdt = LocalDateTime.ofInstant(to, ZoneId.systemDefault());

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

        Map<String, Object> stats = jdbcTemplate.queryForMap(sql, buyerId, fromLdt, toLdt);

        // 2. Orders & Spending Over Time (PostgreSQL)
        String trendSql = """
            SELECT TO_CHAR(created_at, 'YYYY-MM-DD') as date, 
                   COUNT(*) as count, 
                   COALESCE(SUM(total_amount_paise), 0) as amount
            FROM orders
            WHERE buyer_id = ? AND created_at BETWEEN ? AND ? AND status != 'CANCELLED'
            GROUP BY date
            ORDER BY date ASC
        """;
        
        List<ChartPointResponse> ordersOverTime = new ArrayList<>();
        List<ChartPointResponse> spendingOverTime = new ArrayList<>();
        
        jdbcTemplate.query(trendSql, (rs, rowNum) -> {
            String date = rs.getString("date");
            ordersOverTime.add(new ChartPointResponse(date, rs.getLong("count")));
            spendingOverTime.add(new ChartPointResponse(date, rs.getLong("amount")));
            return null;
        }, buyerId, fromLdt, toLdt);

        // 3. Last 5 orders
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
                .total(rs.getLong("total_amount_paise"))
                .createdAt(rs.getTimestamp("created_at").toInstant())
                .build(),
                buyerId
        );

        return BuyerDashboardAnalyticsResponse.builder()
                .totalOrders(((Number) stats.get("total_orders")).longValue())
                .activeShipmentsCount(((Number) stats.get("active_shipments")).longValue())
                .completedOrders(((Number) stats.get("completed_orders")).longValue())
                .totalSpending(((Number) stats.get("total_spending")).longValue())
                .ordersOverTime(ordersOverTime)
                .spendingOverTime(spendingOverTime)
                .lastOrders(lastOrders)
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

        LocalDateTime fromLdt = LocalDateTime.ofInstant(from, ZoneId.systemDefault());
        LocalDateTime toLdt = LocalDateTime.ofInstant(to, ZoneId.systemDefault());

        Map<String, Object> stats = jdbcTemplate.queryForMap(sql, sellerId, fromLdt, toLdt);

        // Payouts (assuming logic based on order status or separate payout table if it existed)
        // For now, mapping 'COMPLETED' as released, others as holding
        String payoutSql = """
             SELECT 
                COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as released,
                COUNT(CASE WHEN status IN ('PAID', 'SHIPPED', 'DELIVERED') THEN 1 END) as holding
             FROM orders
             WHERE seller_id = ? AND created_at BETWEEN ? AND ?
        """;
        Map<String, Object> payoutStats = jdbcTemplate.queryForMap(payoutSql, sellerId, fromLdt, toLdt);

        // 2. Revenue Trend
        String trendSql = """
            SELECT TO_CHAR(created_at, 'YYYY-MM-DD') as date, COALESCE(SUM(total_amount_paise), 0) as amount
            FROM orders
            WHERE seller_id = ? AND created_at BETWEEN ? AND ? AND status != 'CANCELLED'
            GROUP BY date
            ORDER BY date ASC
        """;
        List<RevenueTrendDTO> trend = jdbcTemplate.query(trendSql, (rs, rowNum) -> RevenueTrendDTO.builder()
                .date(rs.getString("date"))
                .amount(rs.getLong("amount"))
                .build(),
                sellerId, fromLdt, toLdt
        );

        // 3. Orders by Status
        String statusSql = """
            SELECT status as name, COUNT(*) as count
            FROM orders
            WHERE seller_id = ? AND created_at BETWEEN ? AND ?
            GROUP BY status
        """;
        List<StatusCountDTO> statusCounts = jdbcTemplate.query(statusSql, (rs, rowNum) -> StatusCountDTO.builder()
                .name(rs.getString("name"))
                .count(rs.getLong("count"))
                .build(),
                sellerId, fromLdt, toLdt
        );

        return SellerDashboardAnalyticsResponse.builder()
                .totalSalesCount(((Number) stats.get("total_sales")).longValue())
                .totalRevenueINRPaise(((Number) stats.get("total_revenue")).longValue())
                .pendingOrdersCount(((Number) stats.get("pending_orders")).longValue())
                .shippedOrdersCount(((Number) stats.get("shipped_orders")).longValue())
                .deliveredOrdersCount(((Number) stats.get("delivered_orders")).longValue())
                .payoutHoldingCount(((Number) payoutStats.get("holding")).longValue())
                .payoutReleasedCount(((Number) payoutStats.get("released")).longValue())
                .revenueOverTime(trend)
                .ordersByStatus(statusCounts)
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
        LocalDateTime fromLdt = LocalDateTime.ofInstant(from, ZoneId.systemDefault());
        LocalDateTime toLdt = LocalDateTime.ofInstant(to, ZoneId.systemDefault());
        Map<String, Object> orderStats = jdbcTemplate.queryForMap(sql, fromLdt, toLdt);

        String userSql = """
             SELECT 
                COUNT(DISTINCT CASE WHEN r.name = 'ROLE_SELLER' THEN u.id END) as new_sellers,
                COUNT(DISTINCT CASE WHEN r.name = 'ROLE_BUYER' THEN u.id END) as new_buyers
             FROM users u
             JOIN user_roles ur ON u.id = ur.user_id
             JOIN roles r ON ur.role_id = r.id
             WHERE u.created_at BETWEEN ? AND ?
        """;
        Map<String, Object> userStats = jdbcTemplate.queryForMap(userSql, fromLdt, toLdt);

         String disputeSql = """
             SELECT COUNT(*) FROM disputes 
             WHERE created_at BETWEEN ? AND ? AND status = 'OPEN'
        """;
        Long disputesOpen = jdbcTemplate.queryForObject(disputeSql, Long.class, fromLdt, toLdt);
        
        // ...
        
        // Error in TargetContent above, let's fix

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
                fromLdt, toLdt
        );

        // Orders Trend
        String orderTrendSql = """
            SELECT TO_CHAR(created_at, 'YYYY-MM-DD') as date, COUNT(*) as count
            FROM orders
            WHERE created_at BETWEEN ? AND ? AND status != 'CANCELLED'
            GROUP BY date
            ORDER BY date ASC
        """;
        List<ChartPointResponse> ordersOverTime = jdbcTemplate.query(orderTrendSql, (rs, rowNum) -> ChartPointResponse.builder()
                .period(rs.getString("date"))
                .value(rs.getLong("count"))
                .build(),
                fromLdt, toLdt
        );

        // Disputes Trend
        String disputeTrendSql = """
            SELECT TO_CHAR(created_at, 'YYYY-MM-DD') as date, COUNT(*) as count
            FROM disputes
            WHERE created_at BETWEEN ? AND ?
            GROUP BY date
            ORDER BY date ASC
        """;
        List<ChartPointResponse> disputesOverTime = jdbcTemplate.query(disputeTrendSql, (rs, rowNum) -> ChartPointResponse.builder()
                .period(rs.getString("date"))
                .value(rs.getLong("count"))
                .build(),
                fromLdt, toLdt
        );

        long gmv = ((Number) orderStats.get("gmv")).longValue();
        return AdminDashboardAnalyticsResponse.builder()
                .platformOrdersCount(((Number) orderStats.get("total_orders")).longValue())
                .platformGMVINRPaise(gmv)
                .platformCommissionINRPaise(gmv / 10) // 10% flat assumption for now
                .disputesOpenCount(disputesOpen != null ? disputesOpen : 0)
                .newSellersCount(((Number) userStats.get("new_sellers")).longValue())
                .newBuyersCount(((Number) userStats.get("new_buyers")).longValue())
                .ordersOverTime(ordersOverTime)
                .disputesOverTime(disputesOverTime)
                .topCountries(topCountries)
                .build();
    }

    // ==========================================
    // ADVANCED SELLER ANALYTICS
    // ==========================================
    public AdvancedSellerAnalyticsResponse getAdvancedSellerAnalytics(UUID sellerId, Instant from, Instant to) {
        LocalDateTime fromLdt = LocalDateTime.ofInstant(from, ZoneId.systemDefault());
        LocalDateTime toLdt = LocalDateTime.ofInstant(to, ZoneId.systemDefault());

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
                sellerId, fromLdt, toLdt
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
                sellerId, fromLdt, toLdt
        );

        // 3. Avg Order Value
        String avgSql = """
            SELECT COALESCE(AVG(total_amount_paise), 0) 
            FROM orders
            WHERE seller_id = ? AND created_at BETWEEN ? AND ? AND status != 'CANCELLED'
        """;
        Long avgOrderValue = jdbcTemplate.queryForObject(avgSql, Long.class, sellerId, fromLdt, toLdt);

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
                sellerId, fromLdt, toLdt
        );
        
        // 5. Product Views (Conversion)
        String viewsSql = "SELECT COUNT(*) FROM product_views pv JOIN products p ON p.id = pv.product_id WHERE p.seller_id = ? AND pv.viewed_at BETWEEN ? AND ?";
        Long totalViews = jdbcTemplate.queryForObject(viewsSql, Long.class, sellerId, fromLdt, toLdt);
        
        String ordersSql = "SELECT COUNT(*) FROM orders WHERE seller_id = ? AND created_at BETWEEN ? AND ? AND status != 'CANCELLED'";
        Long totalOrders = jdbcTemplate.queryForObject(ordersSql, Long.class, sellerId, fromLdt, toLdt);
        
        Double conversionRate = (totalViews != null && totalViews > 0) ? (double) totalOrders / totalViews * 100.0 : 0.0;
        
        return AdvancedSellerAnalyticsResponse.builder()
                .monthlyRevenueChart(revenueChart)
                .salesByCountry(salesByCountry)
                .avgOrderValueINRPaise(avgOrderValue != null ? avgOrderValue : 0)
                .topProducts(topProducts)
                .rfqSuccessRate(0.0) // Will be updated by rfqOpportunityStats if needed
                .totalProductViews(totalViews != null ? totalViews : 0)
                .totalOrdersFromViews(totalOrders)
                .globalConversionRate(conversionRate)
                // New Metrics
                .viewStats(getPeriodicStats(sellerId, "product_views", "viewed_at", true))
                .inquiryStats(getPeriodicStats(sellerId, "chats", "created_at", false))
                .topProductsByViews(getTopProductsByPerformance(sellerId, "VIEWS"))
                .topProductsByInquiries(getTopProductsByPerformance(sellerId, "INQUIRIES"))
                .rfqOpportunityStats(getRfqOpportunityStats(sellerId))
                .topBuyerCountries(getTopBuyerCountries(sellerId))
                .recentActivities(getRecentActivities(sellerId))
                .build();
    }

    private PeriodicStatsResponse getPeriodicStats(UUID sellerId, String table, String dateColumn, boolean joinProducts) {
        String sql;
        
        if (joinProducts) {
            sql = "SELECT " +
                  "COUNT(*) as total, " +
                  "COUNT(CASE WHEN t." + dateColumn + " >= ? THEN 1 END) as today, " +
                  "COUNT(CASE WHEN t." + dateColumn + " >= ? THEN 1 END) as week, " +
                  "COUNT(CASE WHEN t." + dateColumn + " >= ? THEN 1 END) as month " +
                  "FROM " + table + " t JOIN products p ON p.id = t.product_id WHERE p.seller_id = ?";
        } else {
            sql = "SELECT " +
                  "COUNT(*) as total, " +
                  "COUNT(CASE WHEN " + dateColumn + " >= ? THEN 1 END) as today, " +
                  "COUNT(CASE WHEN " + dateColumn + " >= ? THEN 1 END) as week, " +
                  "COUNT(CASE WHEN " + dateColumn + " >= ? THEN 1 END) as month " +
                  "FROM " + table + " WHERE seller_id = ? AND chat_type = 'INQUIRY_CHAT'";
        }

        java.sql.Timestamp today = java.sql.Timestamp.from(Instant.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS));
        java.sql.Timestamp week = java.sql.Timestamp.from(Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS));
        java.sql.Timestamp month = java.sql.Timestamp.from(Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS));

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> PeriodicStatsResponse.builder()
                .total(rs.getLong("total"))
                .today(rs.getLong("today"))
                .thisWeek(rs.getLong("week"))
                .thisMonth(rs.getLong("month"))
                .build(), today, week, month, sellerId);
    }

    private List<ProductPerformanceResponse> getTopProductsByPerformance(UUID sellerId, String type) {
        String sql;
        if ("VIEWS".equals(type)) {
            sql = "SELECT p.id, p.name, COUNT(pv.id) as count FROM products p " +
                  "JOIN product_views pv ON p.id = pv.product_id " +
                  "WHERE p.seller_id = ? GROUP BY p.id, p.name ORDER BY count DESC LIMIT 5";
        } else {
            sql = "SELECT p.id, p.name, COUNT(c.id) as count FROM products p " +
                  "JOIN chats c ON p.id = c.product_id AND c.chat_type = 'INQUIRY_CHAT' " +
                  "WHERE p.seller_id = ? GROUP BY p.id, p.name ORDER BY count DESC LIMIT 5";
        }

        return jdbcTemplate.query(sql, (rs, rowNum) -> ProductPerformanceResponse.builder()
                .productId(UUID.fromString(rs.getString("id")))
                .title(rs.getString("name"))
                .count(rs.getLong("count"))
                .build(), sellerId);
    }

    private RfqOpportunityStatsResponse getRfqOpportunityStats(UUID sellerId) {
        String matchingSql = "SELECT COUNT(*) FROM rfqs r JOIN seller_export_categories sec ON sec.category_id = r.category_id WHERE sec.seller_id = ?";
        String actionSql = "SELECT COUNT(*) as responded, COUNT(CASE WHEN status = 'ACCEPTED' THEN 1 END) as won FROM rfq_quotes WHERE seller_id = ?";

        Long matching = jdbcTemplate.queryForObject(matchingSql, Long.class, sellerId);
        Map<String, Object> actions = jdbcTemplate.queryForMap(actionSql, sellerId);

        return RfqOpportunityStatsResponse.builder()
                .matchingRfqs(matching)
                .respondedRfqs(((Number) actions.get("responded")).longValue())
                .wonRfqs(((Number) actions.get("won")).longValue())
                .build();
    }

    private List<CountrySalesResponse> getTopBuyerCountries(UUID sellerId) {
        String sql = "SELECT bp.country, COUNT(c.id) as count FROM chats c " +
                     "JOIN buyer_profile bp ON bp.id = c.buyer_id " +
                     "WHERE c.seller_id = ? AND c.chat_type = 'INQUIRY_CHAT' " +
                     "GROUP BY bp.country ORDER BY count DESC LIMIT 5";

        return jdbcTemplate.query(sql, (rs, rowNum) -> CountrySalesResponse.builder()
                .country(rs.getString("country"))
                .orders(rs.getLong("count")) // Using 'orders' field to represent inquiry count in this specific DTO usage
                .build(), sellerId);
    }

    private List<RecentActivityResponse> getRecentActivities(UUID sellerId) {
        String sql = """
            SELECT type, description, ts FROM (
                (SELECT 'PRODUCT_VIEW' as type, CONCAT('A buyer viewed your product: ', p.name) as description, pv.viewed_at as ts
                 FROM product_views pv JOIN products p ON p.id = pv.product_id 
                 WHERE p.seller_id = ? 
                 ORDER BY pv.viewed_at DESC LIMIT 5)
                UNION ALL
                (SELECT 'INQUIRY' as type, CONCAT('New inquiry received for: ', p.name) as description, c.created_at as ts
                 FROM chats c JOIN products p ON p.id = c.product_id 
                 WHERE c.seller_id = ? AND c.chat_type = 'INQUIRY_CHAT' 
                 ORDER BY c.created_at DESC LIMIT 5)
                UNION ALL
                (SELECT 'RFQ_QUOTE' as type, CONCAT('You quoted on RFQ: ', r.title) as description, rq.created_at as ts
                 FROM rfq_quotes rq JOIN rfqs r ON r.id = rq.rfq_id 
                 WHERE rq.seller_id = ? 
                 ORDER BY rq.created_at DESC LIMIT 5)
            ) as activities
            ORDER BY ts DESC
            LIMIT 5
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> RecentActivityResponse.builder()
                .type(rs.getString("type"))
                .description(rs.getString("description"))
                .timestamp(rs.getTimestamp("ts").toInstant())
                .build(), sellerId, sellerId, sellerId);
    }
}
