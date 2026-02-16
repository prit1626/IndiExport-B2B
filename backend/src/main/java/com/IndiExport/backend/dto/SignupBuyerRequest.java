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

    private String password;

    private String firstName;
    private String lastName;
    private String country; // ISO-2 country code
    private String companyName; // Optional for buyers
    private String phoneNumber; // Optional
}
