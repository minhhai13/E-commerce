package com.group2.ecommerce.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name cannot be empty")
    private String fullName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email invalid")
    private String email;

    private String phone;

    @NotBlank(message = "Password  cannot be empty")
    @Size(min = 6, message = "Password must be >=6 Characters.")
    private String password;

    @NotBlank(message = "Confirm password.")
    private String confirmPassword;
}