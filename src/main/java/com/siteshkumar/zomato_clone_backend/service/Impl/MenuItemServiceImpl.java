package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.MenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.UpdateMenuItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.UpdateMenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.MenuItemEntity;
import com.siteshkumar.zomato_clone_backend.entity.RestaurantEntity;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
import com.siteshkumar.zomato_clone_backend.enums.Role;
import com.siteshkumar.zomato_clone_backend.exception.ResourceNotFoundException;
import com.siteshkumar.zomato_clone_backend.mapper.MenuItemMapper;
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
    private final MenuItemMapper menuItemMapper;

    @Override
    public CreateMenuItemResponseDto createMenuItem(Long restaurantId, CreateMenuItemRequestDto request) {
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found"));
        
        UserEntity currentUser = authUtils.getCurrentLoggedInUser().getUser();
        if(! restaurant.getOwner().getId().equals(currentUser.getId()))
            throw new AccessDeniedException("You are not allowed to create menu items in this restaurant");

        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setName(request.getName());
        menuItem.setPrice(request.getPrice());
        menuItem.setRestaurant(restaurant);
        menuItem.setAvailable(true);

        MenuItemEntity savedMenuItem = menuItemRepository.save(menuItem);
        
        return menuItemMapper.toCreateResponseDto(savedMenuItem);
    }

    @Override
    public UpdateMenuItemResponseDto updateMenuItem(Long restaurantId, Long menuItemId, UpdateMenuItemRequestDto request) {

            MenuItemEntity menuItem = menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Menu item with id " + menuItemId + " not found in the restaurant id "+restaurantId));

            UserEntity currentUser = authUtils.getCurrentLoggedInUser().getUser();

            if(! menuItem.getRestaurant().getOwner().getId().equals(currentUser.getId()))
                throw new AccessDeniedException("You are not allowed to update this menu item");

            if(request.getName() != null && ! request.getName().isBlank())
                menuItem.setName(request.getName());

            if(request.getPrice() != null && ! request.getPrice().equals(menuItem.getPrice()))
                menuItem.setPrice(request.getPrice());

            MenuItemEntity savedMenuItem = menuItemRepository.save(menuItem);

            return menuItemMapper.toUpdateResponeDto(savedMenuItem);
    }

    @Override
    public void deleteMenuItem(Long restaurantId, Long menuItemId) {
        MenuItemEntity menuItem = menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Menu item with id " + menuItemId + " not found in the restaurant id "+restaurantId));

        UserEntity currentUser = authUtils.getCurrentLoggedInUser().getUser();

        if(! currentUser.getId().equals(menuItem.getRestaurant().getOwner().getId()))
            throw new AccessDeniedException("You are not allowed to delete this menu item");

        // Implementing soft delete
        menuItem.setAvailable(false);
        menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItemResponseDto getMenuItemById(Long restaurantId, Long menuItemId) {
        MenuItemEntity menuItem = menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Menu item with id " + menuItemId + " not found in the restaurant id "+restaurantId));

        if(! menuItem.isAvailable() || ! menuItem.getRestaurant().isActive())
            throw new ResourceNotFoundException("Menu item not available");

        return menuItemMapper.toResponseDto(menuItem);
    }

    @Override
    public Page<MenuItemResponseDto> getAllMenuItems(Long restaurantId, Pageable pageable) {
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found"));

        if (!restaurant.isActive())
            throw new ResourceNotFoundException("Restaurant not available");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String);        

        Page<MenuItemEntity> menuItems;

        if(isLoggedIn){
            UserEntity currentUser = authUtils.getCurrentLoggedInUser().getUser();
            boolean isOwner = currentUser.getRole() == Role.RESTAURANT && restaurant.getOwner().getId().equals(currentUser.getId());

            if(isOwner)
                menuItems = menuItemRepository.findByRestaurantId(restaurantId, pageable);
            
            else
                menuItems = menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId, pageable);
        }

        else
            menuItems = menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId, pageable);

        return menuItems.map(menuItemMapper::toResponseDto);
    }
}
