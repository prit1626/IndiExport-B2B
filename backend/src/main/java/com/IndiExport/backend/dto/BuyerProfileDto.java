package com.IndiExport.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO container for Buyer Profile.
 */
public class BuyerProfileDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BuyerProfileResponse {
        private UUID userId;
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String profilePictureUrl;
        
        private String companyName;
        private String country;
        private String state;
        private String city;
        private String address;
        private String postalCode;
        private String preferredCurrency;
        
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateBuyerProfileRequest {
        @NotBlank(message = "First name is required")
        private String firstName;
        
        @NotBlank(message = "Last name is required")
        private String lastName;
        
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
        private String phoneNumber;
        
        private String companyName;
        
        @NotBlank(message = "Country is required")
        private String country;
        
        private String state;
        private String city;
        private String address;
        private String postalCode;
        
        private String preferredCurrency;
    }
}
