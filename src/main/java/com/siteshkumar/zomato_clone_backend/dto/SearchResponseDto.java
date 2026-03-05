package com.siteshkumar.zomato_clone_backend.dto;

import java.util.List;

import com.siteshkumar.zomato_clone_backend.dto.menuItem.MenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.RestaurantResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDto {

    private List<RestaurantResponseDto> restaurants;
    private List<MenuItemResponseDto> menuItems;
    
}
