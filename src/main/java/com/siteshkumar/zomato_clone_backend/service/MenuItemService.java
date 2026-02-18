package com.siteshkumar.zomato_clone_backend.service;

import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemResponseDto;

public interface MenuItemService {
    CreateMenuItemResponseDto createMenuItem(Long id, CreateMenuItemRequestDto request);
}
