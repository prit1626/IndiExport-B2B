package com.IndiExport.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SignupResponse DTO for POST /api/v1/auth/signup/buyer and POST /api/v1/auth/signup/seller
 * Returns the created user info and login tokens.
 * Uses camelCase JSON.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignupResponse {

    private LoginResponse.UserInfo user;

    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long expiresIn; // Access token expiration in seconds
}
