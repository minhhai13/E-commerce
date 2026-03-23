package com.group2.ecommerce.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @Email(message = "Email is not valid")
    @NotBlank(message = "Email field is required")
    private String email;

    @NotBlank(message = "Password field is required")
    private String password;
}
