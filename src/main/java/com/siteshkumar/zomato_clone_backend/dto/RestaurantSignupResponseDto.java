package com.siteshkumar.zomato_clone_backend.dto;

import com.siteshkumar.zomato_clone_backend.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantSignupResponseDto {
    
    private String restaurantName;
    private String city;
    private String ownerEmail;
    private AccountStatus status;
}
