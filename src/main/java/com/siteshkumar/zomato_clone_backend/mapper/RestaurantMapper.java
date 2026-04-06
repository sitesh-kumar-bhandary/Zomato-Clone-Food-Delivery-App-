package com.siteshkumar.zomato_clone_backend.mapper;

import org.springframework.stereotype.Component;
import com.siteshkumar.zomato_clone_backend.document.RestaurantDocument;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.RestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.RestaurantEntity;

@Component
public class RestaurantMapper {

        public RestaurantResponseDto toResponseDto(RestaurantEntity entity) {
            
        return new RestaurantResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getCity(),
                entity.getRestaurantStatus()
        );
    }

    public RestaurantDocument toDocument(RestaurantEntity entity) {

        return new RestaurantDocument(
                entity.getId().toString(),
                entity.getName(),
                entity.getCity(),
                entity.getRestaurantStatus().name()
        );
    }
}
