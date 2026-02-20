package com.siteshkumar.zomato_clone_backend.service;

import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.UpdateMenuItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.UpdateMenuItemResponseDto;

public interface MenuItemService {
    CreateMenuItemResponseDto createMenuItem(Long restaurantId, CreateMenuItemRequestDto request);
    UpdateMenuItemResponseDto updateMenuItem(Long restaurantId, Long menuItemId, UpdateMenuItemRequestDto request);
}
