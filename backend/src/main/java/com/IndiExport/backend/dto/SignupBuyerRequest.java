package com.IndiExport.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SignupBuyerRequest DTO for POST /auth/signup/buyer
 * Creates a new BUYER user with buyer profile
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupBuyerRequest {

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be 8-50 characters")
    // Pattern: At least one uppercase, one lowercase, one digit, one special char
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

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 2, message = "Country must be a 2-letter ISO code (e.g. IN, US)")
    private String country; // ISO-2 country code

    private String companyName; // Optional for buyers

    @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$", message = "Phone number format invalid")
    private String phoneNumber; // Optional
}
