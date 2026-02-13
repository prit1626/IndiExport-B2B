package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.*;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.*;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AuthService handles authentication and authorization logic.
 * - User signup (buyer/seller)
 * - User login with JWT tokens
 * - Refresh token rotation
 * - Logout (token revocation)
 * - Get authenticated user profile
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final SellerPlanRepository sellerPlanRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginAuditRepository loginAuditRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final SellerKycRepository sellerKycRepository;

    // ===================== SIGNUP =====================

    /**
     * Signup a new BUYER user.
     * Creates User (role BUYER) + BuyerProfile.
     */
    @Transactional
    public SignupResponse signupBuyer(SignupBuyerRequest request) {

        // Check if user already exists
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new ConflictException("User", "email", request.getEmail());
        }

        // Get BUYER role
        Role buyerRole = roleRepository.findByName(Role.RoleType.BUYER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "BUYER"));

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .status(User.UserStatus.ACTIVE)
                .roles(new HashSet<>(Collections.singletonList(buyerRole)))
                .build();

        User savedUser = userRepository.save(user);

        // Create buyer profile
        BuyerProfile buyerProfile = BuyerProfile.builder()
                .user(savedUser)
                .country(request.getCountry())
                .companyName(request.getCompanyName())
                .preferredCurrency("INR")
                .build();

        buyerProfileRepository.save(buyerProfile);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(savedUser.getId().toString(), savedUser.getEmail(), extractRoles(savedUser));
        String refreshToken = jwtService.generateRefreshToken(savedUser.getId().toString(), savedUser.getEmail());

        // Store refresh token in DB
        refreshTokenService.createRefreshToken(savedUser, refreshToken, "Web Browser", "127.0.0.1", "Signup");

        long expiresIn = jwtService.getAccessTokenExpirationInSeconds();

        return SignupResponse.builder()
                .user(buildUserInfo(savedUser, null, null))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .build();
    }

    /**
     * Signup a new SELLER user.
     * Creates User (role SELLER) + SellerProfile + SellerPlan (BASIC_SELLER).
     * IEC status starts as NOT_VERIFIED.
     */
    @Transactional
    public SignupResponse signupSeller(SignupSellerRequest request) {

        // Check if user already exists
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new ConflictException("User", "email", request.getEmail());
        }

        // Get SELLER role
        Role sellerRole = roleRepository.findByName(Role.RoleType.SELLER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "SELLER"));

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .status(User.UserStatus.ACTIVE)
                .roles(new HashSet<>(Collections.singletonList(sellerRole)))
                .build();

        User savedUser = userRepository.save(user);

        // Create seller profile with basic profile fields
        SellerProfile sellerProfile = SellerProfile.builder()
                .user(savedUser)
                .companyName(request.getCompanyName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .businessEmail(request.getEmail())
                .businessPhone(request.getPhoneNumber())
                .totalProducts(0)
                .activeProducts(0)
                .totalOrders(0)
                .totalSalesPaise(0L)
                .averageRatingMilli(0)
                .build();

        SellerProfile savedProfile = sellerProfileRepository.save(sellerProfile);

        // Create KYC entity with fields provided during signup
        SellerKyc sellerKyc = SellerKyc.builder()
                .seller(savedProfile)
                .iecNumber(request.getIecNumber())
                .panNumber(request.getPanNumber())
                .bankAccountNumberMasked(maskAccountNumber(request.getBankAccountNumber()))
                .bankIfscCode(request.getIfscCode())
                .bankAccountHolderName(request.getBankAccountHolderName())
                .verificationStatus(SellerKyc.VerificationStatus.NOT_VERIFIED)
                .build();
        
        sellerKycRepository.save(sellerKyc);
        savedProfile.setKyc(sellerKyc);

        // Create BASIC_SELLER plan
        SellerPlan basicPlan = SellerPlan.createBasicPlan(savedProfile);
        sellerPlanRepository.save(basicPlan);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(savedUser.getId().toString(), savedUser.getEmail(), extractRoles(savedUser));
        String refreshToken = jwtService.generateRefreshToken(savedUser.getId().toString(), savedUser.getEmail());

        // Store refresh token in DB
        refreshTokenService.createRefreshToken(savedUser, refreshToken, "Web Browser", "127.0.0.1", "Signup");

        long expiresIn = jwtService.getAccessTokenExpirationInSeconds();

        return SignupResponse.builder()
                .user(buildUserInfo(savedUser, "BASIC_SELLER", "NOT_VERIFIED"))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .build();
    }

    // ===================== LOGIN =====================

    /**
     * Login user with email and password.
     * Returns access token, refresh token, and user info.
     * Logs login attempt (success/failure).
     */
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {

        // Authenticate — let Spring Security handle bad credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // If authentication succeeded, load user
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        // Check account status
        if (user.getStatus() == User.UserStatus.SUSPENDED) {
            throw new ForbiddenException("Your account has been suspended");
        }
        if (user.getStatus() == User.UserStatus.INACTIVE) {
            throw new ForbiddenException("Your account is inactive");
        }

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user.getId().toString(), user.getEmail(), extractRoles(user));
        String refreshToken = jwtService.generateRefreshToken(user.getId().toString(), user.getEmail());

        // Store refresh token in DB
        String deviceName = request.getDeviceName() != null ? request.getDeviceName() : "Unknown Device";
        refreshTokenService.createRefreshToken(user, refreshToken, deviceName, ipAddress, userAgent);

        // Log successful login
        logLoginAttempt(user, LoginAudit.LoginStatus.SUCCESS, null, request.getEmail(), ipAddress, userAgent);

        long expiresIn = jwtService.getAccessTokenExpirationInSeconds();

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .user(buildLoginUserInfo(user))
                .build();
    }

    // ===================== REFRESH =====================

    /**
     * Refresh access token using refresh token.
     * Implements refresh token ROTATION: old token revoked, new token issued.
     */
    @Transactional
    public LoginResponse refresh(RefreshRequest request) {
        // Validate refresh token
        if (!refreshTokenService.verifyRefreshToken(request.getRefreshToken())) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        RefreshToken storedToken = refreshTokenService.getRefreshToken(request.getRefreshToken());
        User user = storedToken.getUser();

        // Generate NEW access token + NEW refresh token (rotation)
        String newAccessToken = jwtService.generateAccessToken(user.getId().toString(), user.getEmail(), extractRoles(user));
        String newRefreshToken = jwtService.generateRefreshToken(user.getId().toString(), user.getEmail());

        // Rotate: revoke old, store new
        refreshTokenService.rotateRefreshToken(
                request.getRefreshToken(),
                newRefreshToken,
                storedToken.getDeviceName(),
                storedToken.getIpAddress(),
                storedToken.getUserAgent()
        );

        long expiresIn = jwtService.getAccessTokenExpirationInSeconds();

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken) // Return NEW rotated refresh token
                .expiresIn(expiresIn)
                .user(buildLoginUserInfo(user))
                .build();
    }

    // ===================== LOGOUT =====================

    /**
     * Logout user by revoking refresh token
     */
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }

    /**
     * Logout from all devices by revoking all refresh tokens
     */
    @Transactional
    public void logoutAllDevices(UUID userId) {
        refreshTokenService.revokeAllTokensForUser(userId);
    }

    // ===================== ME =====================

    /**
     * Get current authenticated user profile
     */
    @Transactional(readOnly = true)
    public MeResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().toString())
                .collect(Collectors.toList());

        MeResponse response = MeResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .profilePictureUrl(user.getProfilePictureUrl())
                .status(user.getStatus().toString())
                .roles(roles)
                .build();

        // Add buyer details if BUYER
        if (roles.contains("BUYER") && user.getBuyerProfile() != null) {
            BuyerProfile bp = user.getBuyerProfile();
            response.setBuyerDetails(MeResponse.BuyerDetails.builder()
                    .buyerId(bp.getId().toString())
                    .country(bp.getCountry())
                    .companyName(bp.getCompanyName())
                    .address(bp.getAddress())
                    .city(bp.getCity())
                    .state(bp.getState())
                    .postalCode(bp.getPostalCode())
                    .preferredCurrency(bp.getPreferredCurrency())
                    .build());
        }

        // Add seller details if SELLER
        if (roles.contains("SELLER") && user.getSellerProfile() != null) {
            SellerProfile sp = user.getSellerProfile();
            SellerPlan plan = sellerPlanRepository.findBySellerIdAndIsActiveTrue(sp.getId())
                    .orElse(null);

            response.setSellerDetails(MeResponse.SellerDetails.builder()
                    .sellerId(sp.getId().toString())
                    .country(sp.getCountry())
                    .companyName(sp.getCompanyName())
                    .iecNumber(sp.getKyc() != null ? sp.getKyc().getIecNumber() : null)
                    .iecVerificationStatus(sp.getKyc() != null ? sp.getKyc().getVerificationStatus().toString() : "NOT_VERIFIED")
                    .kycStatus(sp.getKyc() != null ? sp.getKyc().getVerificationStatus().toString() : "NOT_VERIFIED")
                    .planType(plan != null ? plan.getPlanType().toString() : null)
                    .maxActiveProducts(plan != null ? plan.getMaxActiveProducts() : 0)
                    .currentActiveProducts(sp.getActiveProducts())
                    .totalRevenue(sp.getTotalSalesPaise() / 100.0)
                    .averageRating(sp.getAverageRatingMilli() / 1000.0)
                    .totalOrdersCount(sp.getTotalOrders())
                    .build());
        }

        return response;
    }

    // ==================== Helper Methods ====================

    private String extractRoles(User user) {
        return user.getRoles().stream()
                .map(role -> "ROLE_" + role.getName())
                .collect(Collectors.joining(","));
    }

    /**
     * Build UserInfo for signup response (role string already known)
     */
    private LoginResponse.UserInfo buildUserInfo(User user, String sellerPlanType, String iecStatus) {
        String role = user.getRoles().stream()
                .map(r -> r.getName().toString())
                .collect(Collectors.joining());

        return LoginResponse.UserInfo.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(role)
                .sellerPlanType(sellerPlanType)
                .iecVerificationStatus(iecStatus)
                .build();
    }

    /**
     * Build UserInfo for login/refresh response (loads seller details if applicable)
     */
    private LoginResponse.UserInfo buildLoginUserInfo(User user) {
        String role = user.getRoles().stream()
                .map(r -> r.getName().toString())
                .collect(Collectors.joining());

        String sellerPlanType = null;
        String iecVerificationStatus = null;

        if (role.equals("SELLER") && user.getSellerProfile() != null) {
            SellerProfile sp = user.getSellerProfile();
            iecVerificationStatus = sp.getKyc() != null ? sp.getKyc().getVerificationStatus().toString() : "NOT_VERIFIED";

            SellerPlan plan = sellerPlanRepository.findBySellerIdAndIsActiveTrue(sp.getId()).orElse(null);
            if (plan != null) {
                sellerPlanType = plan.getPlanType().toString();
            }
        }

        return LoginResponse.UserInfo.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(role)
                .sellerPlanType(sellerPlanType)
                .iecVerificationStatus(iecVerificationStatus)
                .build();
    }

    private void logLoginAttempt(User user, LoginAudit.LoginStatus status, String failureReason, String email, String ipAddress, String userAgent) {
        LoginAudit audit = LoginAudit.builder()
                .user(user)
                .email(email)
                .status(status)
                .failureReason(failureReason)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        loginAuditRepository.save(audit);
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) return "****";
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}
