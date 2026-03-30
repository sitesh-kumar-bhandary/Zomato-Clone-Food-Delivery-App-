package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siteshkumar.zomato_clone_backend.document.MenuItemDocument;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.MenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.UpdateMenuItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.UpdateMenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.MenuItemEntity;
import com.siteshkumar.zomato_clone_backend.entity.RestaurantEntity;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
import com.siteshkumar.zomato_clone_backend.exception.ResourceNotFoundException;
import com.siteshkumar.zomato_clone_backend.mapper.MenuItemMapper;
import com.siteshkumar.zomato_clone_backend.repository.elasticsearch.MenuItemSearchRepository;
import com.siteshkumar.zomato_clone_backend.repository.mysql.MenuItemRepository;
import com.siteshkumar.zomato_clone_backend.repository.mysql.RestaurantRepository;
import com.siteshkumar.zomato_clone_backend.service.MenuItemService;
import com.siteshkumar.zomato_clone_backend.service.MetricsService;
import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemServiceImpl implements MenuItemService {

    private final AuthUtils authUtils;
    private final MetricsService metricsService;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemSearchRepository menuItemSearchRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    @Override
    @Transactional
    public CreateMenuItemResponseDto createMenuItem(Long restaurantId, CreateMenuItemRequestDto request) {

        log.info("Creating menu item. RestaurantId: {}, Name: {}", restaurantId, request.getName());

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> {
                    log.error("Restaurant not found with id: {}", restaurantId);
                    return new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found");
                });

        UserEntity currentUser = authUtils.getCurrentLoggedInUser().getUser();

        if (!restaurant.getOwner().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized menu item creation attempt. UserId: {}, RestaurantId: {}", 
                        currentUser.getId(), restaurantId);
            throw new AccessDeniedException("You are not allowed to create menu items in this restaurant");
        }

        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setName(request.getName());
        menuItem.setPrice(request.getPrice());
        menuItem.setRestaurant(restaurant);
        menuItem.setAvailable(true);

        MenuItemEntity savedMenuItem = menuItemRepository.save(menuItem);

        log.info("Menu item created successfully. MenuItemId: {}", savedMenuItem.getId());

        MenuItemDocument document = menuItemMapper.toDocument(savedMenuItem);
        menuItemSearchRepository.save(document);

        return menuItemMapper.toCreateResponseDto(savedMenuItem);
    }

    @Override
    @Transactional
    @CacheEvict(value = "menuItem", key="#menuItemId")
    public UpdateMenuItemResponseDto updateMenuItem(Long restaurantId, Long menuItemId,
            UpdateMenuItemRequestDto request) {

        log.info("Updating menu item. RestaurantId: {}, MenuItemId: {}", restaurantId, menuItemId);

        MenuItemEntity menuItem = menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)
                .orElseThrow(() -> {
                    log.error("Menu item not found. MenuItemId: {}, RestaurantId: {}", menuItemId, restaurantId);
                    return new ResourceNotFoundException(
                        "Menu item with id " + menuItemId + " not found in the restaurant id " + restaurantId);
                });

        UserEntity currentUser = authUtils.getCurrentLoggedInUser().getUser();

        if (!menuItem.getRestaurant().getOwner().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized menu item update attempt. UserId: {}, MenuItemId: {}", 
                        currentUser.getId(), menuItemId);
            throw new AccessDeniedException("You are not allowed to update this menu item");
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            log.debug("Updating name for MenuItemId: {}", menuItemId);
            menuItem.setName(request.getName());
        }

        if (request.getPrice() != null && !request.getPrice().equals(menuItem.getPrice())) {
            log.debug("Updating price for MenuItemId: {}", menuItemId);
            menuItem.setPrice(request.getPrice());
        }

        MenuItemEntity savedMenuItem = menuItemRepository.save(menuItem);

        log.info("Menu item updated successfully. MenuItemId: {}", menuItemId);

        MenuItemDocument document = menuItemMapper.toDocument(savedMenuItem);
        menuItemSearchRepository.save(document);

        return menuItemMapper.toUpdateResponeDto(savedMenuItem);
    }

    @Override
    @Transactional
    @CacheEvict(value = "menuItem", key="#menuItemId")
    public void deleteMenuItem(Long restaurantId, Long menuItemId) {

        log.info("Deleting menu item (soft delete). RestaurantId: {}, MenuItemId: {}", restaurantId, menuItemId);

        MenuItemEntity menuItem = menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)
                .orElseThrow(() -> {
                    log.error("Menu item not found for deletion. MenuItemId: {}, RestaurantId: {}", 
                                menuItemId, restaurantId);
                    return new ResourceNotFoundException(
                        "Menu item with id " + menuItemId + " not found in the restaurant id " + restaurantId);
                });

        UserEntity currentUser = authUtils.getCurrentLoggedInUser().getUser();

        if (!currentUser.getId().equals(menuItem.getRestaurant().getOwner().getId())) {
            log.warn("Unauthorized menu item delete attempt. UserId: {}, MenuItemId: {}", 
                        currentUser.getId(), menuItemId);
            throw new AccessDeniedException("You are not allowed to delete this menu item");
        }

        menuItem.setAvailable(false);
        menuItemRepository.save(menuItem);

        MenuItemDocument document = menuItemMapper.toDocument(menuItem);
        menuItemSearchRepository.save(document);

        log.info("Menu item soft deleted successfully. MenuItemId: {}", menuItemId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "menuItem", key = "#restaurantId + '_' + #menuItemId")
    public MenuItemResponseDto getMenuItemById(Long restaurantId, Long menuItemId) {
        log.info("ENTERED getMenuItemById METHOD");

        log.info("Fetching menu item (possibly from cache). RestaurantId: {}, MenuItemId: {}", 
                    restaurantId, menuItemId);

        metricsService.incrementDbHits();

        MenuItemEntity menuItem = menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)
                .orElseThrow(() -> {
                    log.error("Menu item not found. MenuItemId: {}, RestaurantId: {}", menuItemId, restaurantId);
                    return new ResourceNotFoundException(
                        "Menu item with id " + menuItemId + " not found in the restaurant id " + restaurantId);
                });

        if (!menuItem.isAvailable() || !menuItem.getRestaurant().isActive()) {
            log.warn("Menu item not available or restaurant inactive. MenuItemId: {}", menuItemId);
            throw new ResourceNotFoundException("Menu item not available");
        }

        log.info("Menu item fetched successfully. MenuItemId: {}", menuItemId);

        return menuItemMapper.toResponseDto(menuItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MenuItemResponseDto> getPublicMenuItems(Long restaurantId, Pageable pageable) {

        log.info("Fetching public menu items. RestaurantId: {}, Page: {}", restaurantId, pageable);

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> {
                    log.error("Restaurant not found. RestaurantId: {}", restaurantId);
                    return new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found");
                });

        if (!restaurant.isActive()) {
            log.warn("Restaurant not active. RestaurantId: {}", restaurantId);
            throw new ResourceNotFoundException("Restaurant not available");
        }

        Page<MenuItemEntity> menuItems = menuItemRepository
                .findByRestaurantIdAndAvailableTrue(restaurantId, pageable);

        log.info("Public menu items fetched successfully. Count: {}", menuItems.getTotalElements());

        return menuItems.map(menuItemMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MenuItemResponseDto> getOwnerMenuItems(Long restaurantId, Pageable pageable) {

        log.info("Fetching owner menu items. RestaurantId: {}, Page: {}", restaurantId, pageable);

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> {
                log.error("Restaurant not found. RestaurantId: {}", restaurantId);
                return new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found");
            });

        UserEntity currentUser = authUtils.getCurrentLoggedInUser().getUser();

        if (!restaurant.getOwner().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized access to owner menu. UserId: {}, RestaurantId: {}", 
                        currentUser.getId(), restaurantId);
            throw new AccessDeniedException("You are not allowed to access this menu");
        }

        Page<MenuItemEntity> menuItems = menuItemRepository.findByRestaurantId(restaurantId, pageable);

        log.info("Owner menu items fetched successfully. Count: {}", menuItems.getTotalElements());

        return menuItems.map(menuItemMapper::toResponseDto);
    }

}