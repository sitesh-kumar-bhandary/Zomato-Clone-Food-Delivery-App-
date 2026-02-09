package com.siteshkumar.zomato_clone_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSignupRequestDto {
    
    @NotBlank(message="Name is required")
    private String name;

    @Email(message="Invalid email address")
    @NotBlank(message="Email is required")
    private String email;

    @Size(min=8, message = "Password must be at least 8 characters")
    @NotBlank(message="Password is required")
    private String password;
}
