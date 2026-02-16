package com.IndiExport.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SignupSellerRequest DTO for POST /api/v1/auth/signup/seller
 * Creates a new SELLER user with:
 * - User (role SELLER)
 * - SellerProfile (IEC = NOT_VERIFIED)
 * - SellerPlan = BASIC_SELLER
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupSellerRequest {

    @Email(message = "Email must be valid")
    private String email;

    private String password;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String companyName;
    private String iecNumber;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    
    @Builder.Default
    private String country = "IN"; // Sellers must be from India

    // Bank details for seller payouts
    private String bankAccountNumber;
    private String ifscCode;
    private String bankAccountHolderName;
    private String panNumber;
}
