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
 * Seller Controller
 * Protected endpoints for SELLER role users
 * All endpoints require valid JWT access token with SELLER role
 */
@RestController
@RequestMapping("/api/v1/sellers")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class SellerController {

    /**
     * GET /api/v1/sellers/dashboard
     * Get seller dashboard with metrics
     * Only accessible to SELLER role
     * 
     * @return Dashboard data with products, revenue, orders
     */
    private final com.IndiExport.backend.service.ProductService productService;
    private final com.IndiExport.backend.service.OrderService orderService;
    private final com.IndiExport.backend.repository.UserRepository userRepository;

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object details = auth.getDetails();
        if (details instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            return UUID.fromString(((JwtAuthenticationFilter.JwtAuthenticationDetails) details).getUserId());
        }
        // Fallback or legacy (should be covered by filter)
         return userRepository.findByEmail(auth.getName()).orElseThrow().getId();
    }

    /**
     * GET /api/v1/sellers/dashboard
     * Get seller dashboard with metrics
     * Only accessible to SELLER role
     * 
     * @return Dashboard data with products, revenue, orders
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Object>> getSellerDashboard() {
        UUID sellerId = getCurrentUserId();
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("message", "Seller dashboard for: " + sellerId);
        dashboard.put("activeProducts", 0);
        dashboard.put("totalRevenue", 0.0);
        dashboard.put("ordersCount", 0);
        dashboard.put("averageRating", 0.0);
        dashboard.put("recentOrders", new ArrayList<>());
        
        return ResponseEntity.ok(dashboard);
    }

    /**
     * GET /api/v1/sellers/products
     * Get seller's products
     * Available to all SELLER users (both BASIC and ADVANCED)
     * 
     * @param page Pagination page number
     * @return Paginated products list
     */
    @GetMapping("/products")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Object>> getSellerProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        
        UUID sellerId = getCurrentUserId();
        log.info("API Request: getSellerProducts. User: {}, Page: {}, Status: {}", sellerId, page, status);
        
        org.springframework.data.domain.Page<com.IndiExport.backend.dto.ProductDto.ProductResponse> productPage = 
            productService.getSellerProducts(sellerId, page, size, keyword, status);
        
        log.info("API Response: getSellerProducts. Items found: {}", productPage.getTotalElements());
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", productPage.getContent());
        response.put("page", productPage.getNumber());
        response.put("size", productPage.getSize());
        response.put("totalElements", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());
        response.put("last", productPage.isLast());
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/sellers/orders
     * Get seller's orders
     * 
     * @param page Pagination page number
     * @return Paginated orders list
     */
    @GetMapping("/orders")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Object>> getSellerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort) {
        
        UUID sellerId = getCurrentUserId();
        return ResponseEntity.ok(orderService.getSellerOrders(sellerId, page, size, status, sort));
    }

    /**
     * GET /api/v1/sellers/orders/{orderId}
     * Get seller order details
     */
    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Object>> getSellerOrderDetails(@PathVariable UUID orderId) {
        UUID sellerId = getCurrentUserId();
        return ResponseEntity.ok(orderService.getSellerOrderDetails(sellerId, orderId));
    }

    /**
     * GET /api/v1/sellers/advanced-analytics
     * Get advanced analytics (ADVANCED_SELLER only)
     * Should be restricted to sellers with ADVANCED_SELLER plan
     * Implementation: Check plan in service layer
     * 
     * @return Advanced analytics data
     */
    @GetMapping("/advanced-analytics")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Object>> getAdvancedAnalytics() {
        UUID sellerId = getCurrentUserId();
        
        // TODO: Check seller plan in service - throw BusinessRuleViolationException if BASIC_SELLER
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("conversionRate", "3.5%");
        analytics.put("customerSegments", new ArrayList<>());
        analytics.put("monthlyTrends", new ArrayList<>());
        analytics.put("productPerformance", new ArrayList<>());
        
        return ResponseEntity.ok(analytics);
    }

    /**
     * GET /api/v1/sellers/kyc-status
     * Get seller KYC status and verification details
     * 
     * @return KYC status and pending requirements
     */
    @GetMapping("/kyc-status")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Object>> getKycStatus() {
        UUID sellerId = getCurrentUserId();
        
        Map<String, Object> kycStatus = new HashMap<>();
        kycStatus.put("iecVerificationStatus", "NOT_VERIFIED");
        kycStatus.put("kycStatus", "NOT_VERIFIED");
        kycStatus.put("pendingDocuments", new ArrayList<>());
        kycStatus.put("lastUpdated", new Date());
        
        return ResponseEntity.ok(kycStatus);
    }


}
