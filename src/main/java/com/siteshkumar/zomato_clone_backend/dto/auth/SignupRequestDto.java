package com.siteshkumar.zomato_clone_backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank(message="Name is required")
    private String name;

    @Email(message="Invalid email address")
    @NotBlank(message="Email is required")
    private String email;

    @NotBlank(message="Phone number is required")
    @Pattern(regexp = "\\d{10}", message="Phone number must be 10 digits")
    private String phone;

    @NotBlank(message="Password is required")
    @Size(min=8, message = "Password must be at least 8 characters")
    private String password;
}
