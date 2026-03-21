package com.group2.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "Phone must be a valid Vietnamese phone number (10-11 digits)")
    private String userPhone;

    private String recipientName;

    @Pattern(regexp = "^$|^(\\+84|0)\\d{9,10}$", message = "Phone must be a valid Vietnamese phone number (10-11 digits)")
    private String addressPhone;

    private String addressDetail;
}
