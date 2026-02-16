package com.siteshkumar.zomato_clone_backend.dto.restaurant;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRestaurantRequestDto {

    @NotBlank(message="Restaurant name is required")
    private String name;

    @NotBlank(message="City name is required")
    private String city;

}
