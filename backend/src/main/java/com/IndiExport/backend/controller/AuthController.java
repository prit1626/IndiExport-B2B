package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.*;
import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Authentication Controller
 * Handles all authentication endpoints: signup, login, refresh, logout, profile
 * 
 * Endpoints:
 * - POST /auth/signup/buyer - Create buyer account
 * - POST /auth/signup/seller - Create seller account
 * - POST /auth/login - Login with credentials
 * - POST /auth/refresh - Get new access token
 * - POST /auth/logout - Logout current device
 * - POST /auth/logout-all - Logout all devices
 * - GET /auth/me - Get current user profile
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/signup/buyer
     * Create new BUYER user account
     * 
     * @param request SignupBuyerRequest with email, password, name, company details
     * @return SignupResponse with user info and tokens
     */
    @PostMapping("/signup/buyer")
    public ResponseEntity<SignupResponse> signupBuyer(@Valid @RequestBody SignupBuyerRequest request) {
        SignupResponse response = authService.signupBuyer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/v1/auth/signup/seller
     * Create new SELLER user account with BASIC_SELLER plan
     * 
     * @param request SignupSellerRequest with email, password, company, IEC, bank details
     * @return SignupResponse with user info and tokens
     */
    @PostMapping("/signup/seller")
    public ResponseEntity<SignupResponse> signupSeller(@Valid @RequestBody SignupSellerRequest request) {
        SignupResponse response = authService.signupSeller(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/v1/auth/login
     * Login user with email and password
     * Extracts IP address and User-Agent from request for device tracking
     * Logs login attempt (success/failure)
     * 
     * @param request LoginRequest with email, password, deviceName
     * @param httpRequest HttpServletRequest to extract IP and User-Agent
     * @return LoginResponse with access token, refresh token, user info
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        LoginResponse response = authService.login(request, ipAddress, userAgent);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/refresh
     * Get new access token using refresh token
     * Optionally rotates refresh token (single-use pattern)
     * 
     * @param request RefreshRequest with refreshToken
     * @return LoginResponse with new access token and possibly rotated refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        LoginResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/logout
     * Logout user by revoking refresh token
     * Only revokes the refresh token provided (current device)
     * Requires valid access token
     * 
     * @param request RefreshRequest with refreshToken to revoke
     * @return Success message
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.getRefreshToken());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully logged out");
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/logout-all
     * Logout from all devices by revoking all refresh tokens
     * Requires valid access token
     * Extracts userId from JWT authentication
     * 
     * @return Success message
     */
    @PostMapping("/logout-all")
    public ResponseEntity<Map<String, String>> logoutAllDevices() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = extractUserIdFromAuth(auth);
        
        authService.logoutAllDevices(userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully logged out from all devices");
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/auth/me
     * Get current authenticated user profile
     * Returns full user info including buyer/seller details
     * Requires valid access token
     * Extracts userId from JWT authentication
     * 
     * @return MeResponse with complete user profile
     */
    @GetMapping("/me")
    public ResponseEntity<MeResponse> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = extractUserIdFromAuth(auth);
        
        MeResponse response = authService.getCurrentUser(userId);
        return ResponseEntity.ok(response);
    }

    // ==================== Helper Methods ====================

    /**
     * Extract client IP address from HttpServletRequest
     * Checks X-Forwarded-For header first (for reverse proxies)
     * Falls back to remoteAddr if header not present
     * 
     * @param request HttpServletRequest
     * @return IP address string
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take first one
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Extract userId from Spring Security Authentication
     * Retrieves userId from JwtAuthenticationDetails set by JwtAuthenticationFilter
     * 
     * @param auth Authentication from SecurityContext
     * @return UUID of authenticated user
     * @throws IllegalStateException if userId not found in authentication
     */
    private UUID extractUserIdFromAuth(Authentication auth) {
        Object details = auth.getDetails();
        if (details instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            String userId = ((JwtAuthenticationFilter.JwtAuthenticationDetails) details).getUserId();
            return UUID.fromString(userId);
        }
        throw new IllegalStateException("User ID not found in authentication");
    }
}
