package com.IndiExport.backend.controller;

import com.IndiExport.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Buyer Controller
 * Protected endpoints for BUYER role users
 * All endpoints require valid JWT access token with BUYER role
 */
@RestController
@RequestMapping("/api/v1/buyer")
@RequiredArgsConstructor
public class BuyerController {

    /**
     * GET /api/v1/buyers/dashboard
     * Get buyer dashboard with recent orders and RFQs
     * Only accessible to BUYER role
     * 
     * @return Dashboard data with orders and RFQs
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Map<String, Object>> getBuyerDashboard() {
        UUID userId = getCurrentUserId();
        
        // Use OrderService to fetch recent orders
        Map<String, Object> ordersData = orderService.getBuyerOrders(userId, 0, 5, null, "createdAt,desc");
        List<Map<String, Object>> recentOrders = (List<Map<String, Object>>) ordersData.get("items");
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("recentOrders", recentOrders);
        dashboard.put("activeRFQs", new ArrayList<>()); // RFQ service not yet integrated here
        dashboard.put("totalSpent", recentOrders.stream()
                .mapToLong(o -> (long) o.getOrDefault("grandTotalINRPaise", 0L))
                .sum() / 100.0);
        dashboard.put("ordersCount", ordersData.get("totalItems"));
        
        return ResponseEntity.ok(dashboard);
    }

    /**
     * GET /api/v1/buyers/dashboard/analytics
     * Get buyer dashboard analytics stats
     * 
     * @return Analytics data
     */
    @GetMapping("/dashboard/analytics")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Map<String, Object>> getBuyerDashboardAnalytics() {
        UUID userId = getCurrentUserId();
        Map<String, Object> ordersData = orderService.getBuyerOrders(userId, 0, 1000, null, null);
        
        long totalOrders = (long) ordersData.getOrDefault("totalItems", 0L);
        List<Map<String, Object>> items = (List<Map<String, Object>>) ordersData.getOrDefault("items", new ArrayList<>());
        
        double totalSpending = items.stream()
                .mapToLong(o -> (long) o.getOrDefault("grandTotalINRPaise", 0L))
                .sum() / 100.0;

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalOrders", totalOrders);
        analytics.put("activeShipmentsCount", items.stream()
                .filter(o -> "SHIPPED".equals(o.get("status")) || "IN_TRANSIT".equals(o.get("status")))
                .count());
        analytics.put("totalSpending", totalSpending);
        
        return ResponseEntity.ok(analytics);
    }

    private final com.IndiExport.backend.service.OrderService orderService;

    /**
     * GET /api/v1/buyers/orders
     * Get buyer's orders
     * 
     * @param page Pagination page number
     * @return Paginated orders list
     */
    @GetMapping("/orders")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Map<String, Object>> getBuyerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort) {
        UUID buyerId = getCurrentUserId();
        return ResponseEntity.ok(orderService.getBuyerOrders(buyerId, page, size, status, sort));
    }

    /**
     * GET /api/v1/buyers/orders/{id}
     * Get single order details
     */
    @GetMapping("/orders/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Map<String, Object>> getBuyerOrderDetails(@PathVariable UUID id) {
        UUID buyerId = getCurrentUserId();
        return ResponseEntity.ok(orderService.getBuyerOrderDetails(buyerId, id));
    }

    // Removed getBuyerOrderTracking to prevent ambiguity with BuyerOrderTrackingController

    /**
     * GET /api/v1/buyers/rfqs
     * Get buyer's RFQs
     * 
     * @param page Pagination page number
     * @return Paginated RFQs list
     */
    @GetMapping("/rfqs")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Map<String, Object>> getBuyerRFQs(
            @RequestParam(defaultValue = "0") int page) {
        UUID buyerId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        response.put("page", page);
        response.put("totalRFQs", 0);
        response.put("rfqs", new ArrayList<>());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Extract userId from Spring Security Authentication
     * Used internally to identify the current authenticated buyer
     * 
     * @return UUID of authenticated buyer
     */
    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object details = auth.getDetails();
        if (details instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            return UUID.fromString(((JwtAuthenticationFilter.JwtAuthenticationDetails) details).getUserId());
        }
        throw new IllegalStateException("User not authenticated");
    }
}
