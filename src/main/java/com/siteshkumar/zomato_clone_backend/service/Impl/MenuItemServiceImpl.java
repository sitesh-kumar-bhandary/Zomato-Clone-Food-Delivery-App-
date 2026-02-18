package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.MenuItemEntity;
import com.siteshkumar.zomato_clone_backend.entity.RestaurantEntity;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
import com.siteshkumar.zomato_clone_backend.exception.ResourceNotFoundException;
import com.siteshkumar.zomato_clone_backend.repository.MenuItemRepository;
import com.siteshkumar.zomato_clone_backend.repository.RestaurantRepository;
import com.siteshkumar.zomato_clone_backend.service.MenuItemService;
import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final AuthUtils authUtils;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public CreateMenuItemResponseDto createMenuItem(Long id, CreateMenuItemRequestDto request) {
        RestaurantEntity restaurant = restaurantRepository.findById(id)
                                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found"));
        
        UserEntity currentUser = authUtils.getCurrentLoggedInUser().getUser();
        if(! restaurant.getOwner().getId().equals(currentUser.getId()))
            throw new AccessDeniedException("You are not allowed to create menu items in this restaurant");

        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setName(request.getName());
        menuItem.setPrice(request.getPrice());
        menuItem.setRestaurant(restaurant);
        menuItem.setAvailable(true);

        MenuItemEntity savedMenuItem = menuItemRepository.save(menuItem);
        
        return new CreateMenuItemResponseDto(
            savedMenuItem.getId(),
            savedMenuItem.getName(),
            savedMenuItem.getPrice(),
            savedMenuItem.isAvailable()
        );
    }
}
