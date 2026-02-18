package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurants/{restaurantId}/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<CreateMenuItemResponseDto> createMenuItem(@PathVariable Long restaurantId , @Valid @RequestBody CreateMenuItemRequestDto request){
        CreateMenuItemResponseDto response = menuItemService.createMenuItem(restaurantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // public ResponseEntity<> updateMenuItem(){

    // }

    // public ResponseEntity<> deleteMenuItem(){

    // }

    // public ResponseEntity<> getMenuItem(){

    // }
}
