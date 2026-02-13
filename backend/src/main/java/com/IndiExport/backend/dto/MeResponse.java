package com.IndiExport.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MeResponse DTO for GET /auth/me
 * Returns current authenticated user's profile information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeResponse {

    private String id; // User UUID

    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String profilePictureUrl;

    private String status; // ACTIVE, INACTIVE, SUSPENDED, DELETED

    private List<String> roles; // ["BUYER"] or ["SELLER"] or ["ADMIN"]

    // Buyer-specific fields
    private BuyerDetails buyerDetails; // Non-null if user has BUYER role

    // Seller-specific fields
    private SellerDetails sellerDetails; // Non-null if user has SELLER role

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BuyerDetails {
        private String buyerId;
        private String country;
        private String companyName;
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private String preferredCurrency;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SellerDetails {
        private String sellerId;
        private String country;
        private String companyName;
        private String iecNumber;
        private String iecVerificationStatus; // NOT_VERIFIED, PENDING, VERIFIED, REJECTED
        private String kycStatus; // NOT_VERIFIED, PENDING, VERIFIED, REJECTED
        private String planType; // BASIC_SELLER, ADVANCED_SELLER
        private Integer maxActiveProducts; // 5 or unlimited
        private Integer currentActiveProducts;
        private Double totalRevenue;
        private Double averageRating;
        private Integer totalOrdersCount;
    }
}
