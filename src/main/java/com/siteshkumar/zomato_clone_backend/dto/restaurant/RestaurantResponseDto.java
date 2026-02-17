package com.siteshkumar.zomato_clone_backend.dto.restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponseDto {
    
    private Long id;
    private String name;
    private String city;

}
