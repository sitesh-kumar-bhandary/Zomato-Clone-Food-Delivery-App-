package com.siteshkumar.zomato_clone_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.MenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.UpdateMenuItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.UpdateMenuItemResponseDto;

public interface MenuItemService {
    CreateMenuItemResponseDto createMenuItem(Long restaurantId, CreateMenuItemRequestDto request);
    UpdateMenuItemResponseDto updateMenuItem(Long restaurantId, Long menuItemId, UpdateMenuItemRequestDto request);
    void deleteMenuItem(Long restaurantId, Long menuItemId);
    MenuItemResponseDto getMenuItemById(Long restaurantId, Long menuItemId);
    Page<MenuItemResponseDto> getAllMenuItems(Long restaurantId, Pageable pageable);
}
