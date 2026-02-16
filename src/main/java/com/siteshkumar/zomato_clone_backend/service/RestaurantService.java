package com.siteshkumar.zomato_clone_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.siteshkumar.zomato_clone_backend.dto.restaurant.CreateRestaurantRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.CreateRestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.RestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.UpdateRestaurantRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.UpdateRestaurantResponseDto;

public interface RestaurantService {
    
    CreateRestaurantResponseDto createRestaurant(CreateRestaurantRequestDto request);
    UpdateRestaurantResponseDto updateRestaurant(Long id, UpdateRestaurantRequestDto request);
    void deleteRestaurant(Long id);
    Page<RestaurantResponseDto> getAllRestaurants(String city, Pageable pageable);
    RestaurantResponseDto getRestaurantById(Long id);

}
