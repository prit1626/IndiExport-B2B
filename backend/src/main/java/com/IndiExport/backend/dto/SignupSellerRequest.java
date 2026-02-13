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
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be 8-50 characters")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "Password must contain uppercase, lowercase, digit, and special character"
    )
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be 2-50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be 2-50 characters")
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$", message = "Phone number format invalid")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Company name is required for sellers")
    @Size(min = 3, max = 100, message = "Company name must be 3-100 characters")
    private String companyName;

    // IEC (Importer-Exporter Code) - India export identification
    @NotBlank(message = "IEC number is required")
    @Pattern(regexp = "^[0-9]{10}[A-Z]{2}[0-9]{4}$", message = "Invalid IEC number format")
    private String iecNumber;

    // Address fields
    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must be at most 100 characters")
    private String state;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code must be at most 20 characters")
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 2, message = "Country must be a 2-letter ISO code (e.g. IN)")
    @Builder.Default
    private String country = "IN"; // Sellers must be from India

    // Bank details for seller payouts
    @NotBlank(message = "Bank account number is required")
    @Pattern(regexp = "^[0-9]{9,18}$", message = "Invalid bank account number")
    private String bankAccountNumber;

    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "^[A-Z]{4}[0]{1}[A-Z0-9]{6}$", message = "Invalid IFSC code")
    private String ifscCode;

    @NotBlank(message = "Bank account holder name is required")
    @Size(min = 2, max = 100, message = "Account holder name must be 2-100 characters")
    private String bankAccountHolderName;

    @NotBlank(message = "PAN number is required")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN format")
    private String panNumber;
}
