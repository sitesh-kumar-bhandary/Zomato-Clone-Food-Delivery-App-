package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.siteshkumar.zomato_clone_backend.dto.CreateRestaurantRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.CreateRestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.RestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.UpdateRestaurantRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.UpdateRestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.service.RestaurantService;


@Service
public class RestaurantServiceImpl implements RestaurantService{

    @Override
    public CreateRestaurantResponseDto createRestaurant(CreateRestaurantRequestDto request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createRestaurant'");
    }

    @Override
    public UpdateRestaurantResponseDto updateRestaurant(Long id, UpdateRestaurantRequestDto request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateRestaurant'");
    }

    @Override
    public void deleteRestaurant(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRestaurant'");
    }

    @Override
    public Page<RestaurantResponseDto> getAllRestaurants(String city, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllRestaurants'");
    }

    @Override
    public RestaurantResponseDto getRestaurantById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRestaurantById'");
    }
    
}
