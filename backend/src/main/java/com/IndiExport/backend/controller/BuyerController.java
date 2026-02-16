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
@RequestMapping("/api/v1/buyers")
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
        UUID buyerId = getCurrentUserId();
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("message", "Buyer dashboard for: " + buyerId);
        dashboard.put("recentOrders", new ArrayList<>());
        dashboard.put("activeRFQs", new ArrayList<>());
        dashboard.put("totalSpent", 0.0);
        dashboard.put("ordersCount", 0);
        
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
        // UUID buyerId = getCurrentUserId(); // In future use to fetch real stats
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalOrders", 0);
        analytics.put("activeShipmentsCount", 0);
        analytics.put("totalSpending", 0.0);
        
        return ResponseEntity.ok(analytics);
    }

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
            @RequestParam(defaultValue = "0") int page) {
        UUID buyerId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        response.put("page", page);
        response.put("totalOrders", 0);
        response.put("orders", new ArrayList<>());
        
        return ResponseEntity.ok(response);
    }

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
