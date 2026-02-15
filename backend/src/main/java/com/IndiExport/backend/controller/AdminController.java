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
 * Admin Controller
 * Protected endpoints for ADMIN role users only
 * All endpoints require valid JWT access token with ADMIN role
 * Admin has full access to manage users, sellers, and platform settings
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    /**
     * GET /api/v1/admin/dashboard
     * Get admin dashboard with platform metrics
     * Only accessible to ADMIN role
     * 
     * @return Dashboard with platform statistics
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        UUID adminId = getCurrentUserId();
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalUsers", 0);
        dashboard.put("totalBuyers", 0);
        dashboard.put("totalSellers", 0);
        dashboard.put("totalOrders", 0);
        dashboard.put("totalRevenue", 0.0);
        dashboard.put("platformHealth", "HEALTHY");
        
        return ResponseEntity.ok(dashboard);
    }

    /**
     * GET /api/v1/admin/users
     * Get all users with pagination
     * Only accessible to ADMIN role
     * 
     * @param page Pagination page number
     * @param role Filter by role (optional)
     * @return Paginated users list
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String role) {
        UUID adminId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        response.put("page", page);
        response.put("totalUsers", 0);
        response.put("users", new ArrayList<>());
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/admin/users/{userId}
     * Get specific user details
     * Only accessible to ADMIN role
     * 
     * @param userId User ID to retrieve
     * @return User details with all profiles
     */
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable UUID userId) {
        UUID adminId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", userId);
        response.put("email", "user@example.com");
        response.put("role", "SELLER");
        response.put("status", "ACTIVE");
        response.put("createdAt", new Date());
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/admin/suspend-user/{userId}
     * Suspend user account
     * Only accessible to ADMIN role
     * 
     * @param userId User ID to suspend
     * @return Success message
     */
    @PostMapping("/suspend-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> suspendUser(@PathVariable UUID userId) {
        UUID adminId = getCurrentUserId();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "User suspended: " + userId);
        response.put("suspendedAt", new Date().toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/admin/reactivate-user/{userId}
     * Reactivate suspended user account
     * Only accessible to ADMIN role
     * 
     * @param userId User ID to reactivate
     * @return Success message
     */
    @PostMapping("/reactivate-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> reactivateUser(@PathVariable UUID userId) {
        UUID adminId = getCurrentUserId();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "User reactivated: " + userId);
        response.put("reactivatedAt", new Date().toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/admin/sellers
     * Get all sellers with pagination
     * Only accessible to ADMIN role
     * 
     * @param page Pagination page number
     * @param iecStatus Filter by IEC verification status (optional)
     * @return Paginated sellers list
     */
    @GetMapping("/sellers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String iecStatus) {
        UUID adminId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        response.put("page", page);
        response.put("totalSellers", 0);
        response.put("sellers", new ArrayList<>());
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/admin/sellers/{sellerId}/verify-iec
     * Verify seller IEC number
     * Only accessible to ADMIN role
     * 
     * @param sellerId Seller ID
     * @return Verification result
     */
    @PostMapping("/sellers/{sellerId}/verify-iec")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> verifySellerIEC(@PathVariable UUID sellerId) {
        UUID adminId = getCurrentUserId();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "IEC verified for seller: " + sellerId);
        response.put("verificationStatus", "VERIFIED");
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/admin/sellers/{sellerId}/reject-iec
     * Reject seller IEC number
     * Only accessible to ADMIN role
     * 
     * @param sellerId Seller ID
     * @param reason Rejection reason
     * @return Rejection result
     */
    @PostMapping("/sellers/{sellerId}/reject-iec")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> rejectSellerIEC(
            @PathVariable UUID sellerId,
            @RequestParam String reason) {
        UUID adminId = getCurrentUserId();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "IEC rejected for seller: " + sellerId);
        response.put("verificationStatus", "REJECTED");
        response.put("reason", reason);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/admin/orders
     * Get all orders with pagination
     * Only accessible to ADMIN role
     * 
     * @param page Pagination page number
     * @param status Filter by order status (optional)
     * @return Paginated orders list
     */
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status) {
        UUID adminId = getCurrentUserId();
        
        Map<String, Object> response = new HashMap<>();
        response.put("page", page);
        response.put("totalOrders", 0);
        response.put("orders", new ArrayList<>());
        
        return ResponseEntity.ok(response);
    }


    /**
     * Extract userId from Spring Security Authentication
     * Used internally to identify the current authenticated admin
     * 
     * @return UUID of authenticated admin
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
