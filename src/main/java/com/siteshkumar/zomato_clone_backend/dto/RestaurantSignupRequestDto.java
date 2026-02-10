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
public class RestaurantSignupRequestDto {

    @NotBlank(message="Restaurant name is required")
    private String restaurantName;

    @NotBlank(message="City is required")
    private String city;

    @Email
    @NotBlank(message="Restaurant official email is required")
    private String ownerEmail;

    @Size(min=8, message="Password must be atleast 8 characters")
    @NotBlank(message="Password is required")
    private String ownerPassword;
}
