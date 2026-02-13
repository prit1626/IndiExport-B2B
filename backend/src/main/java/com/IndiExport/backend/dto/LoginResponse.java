package com.IndiExport.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginResponse DTO for POST /api/v1/auth/login and POST /api/v1/auth/refresh
 * Returns access token, refresh token, and user information.
 * Uses camelCase JSON (no @JsonProperty snake_case).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    private String accessToken;   // JWT access token (short-lived)
    private String refreshToken;  // JWT refresh token (long-lived)

    @Builder.Default
    private String tokenType = "Bearer";

    private Long expiresIn;       // Access token expiration in seconds

    private UserInfo user;        // Logged-in user details

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserInfo {
        private String id;
        private String email;
        private String firstName;
        private String lastName;
        private String role;                   // BUYER, SELLER, ADMIN
        private String sellerPlanType;         // BASIC_SELLER or ADVANCED_SELLER (null for non-sellers)
        private String iecVerificationStatus;  // Seller IEC status (null for buyers)
    }
}
