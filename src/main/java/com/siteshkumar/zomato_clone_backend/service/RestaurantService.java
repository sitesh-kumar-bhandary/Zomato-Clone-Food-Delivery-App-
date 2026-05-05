package com.siteshkumar.zomato_clone_backend.service;

import java.util.List;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.CreateRestaurantRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.RestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.UpdateRestaurantRequestDto;

public interface RestaurantService {
    
    RestaurantResponseDto createRestaurant(CreateRestaurantRequestDto request);
    RestaurantResponseDto updateRestaurant(Long id, UpdateRestaurantRequestDto request);
    void deleteRestaurant(Long id);
    List<RestaurantResponseDto> getAllRestaurants(String city);
    RestaurantResponseDto getRestaurantById(Long id);

}
