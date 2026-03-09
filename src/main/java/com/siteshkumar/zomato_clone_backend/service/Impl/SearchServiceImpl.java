package com.siteshkumar.zomato_clone_backend.service.Impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siteshkumar.zomato_clone_backend.dto.SearchResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.MenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.RestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.MenuItemEntity;
import com.siteshkumar.zomato_clone_backend.entity.RestaurantEntity;
import com.siteshkumar.zomato_clone_backend.repository.MenuItemRepository;
import com.siteshkumar.zomato_clone_backend.repository.RestaurantRepository;
import com.siteshkumar.zomato_clone_backend.service.SearchService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    @Transactional(readOnly = true)
    public SearchResponseDto search(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<RestaurantEntity> restaurants = 
                            restaurantRepository.findByNameContainingIgnoreCase(query, pageable);

         Page<MenuItemEntity> items =
                            menuItemRepository.findByNameContainingIgnoreCase(query, pageable);

        return mapToResponse(restaurants, items);
    }

    private SearchResponseDto mapToResponse(Page<RestaurantEntity> restaurants, Page<MenuItemEntity> items){
        List<RestaurantResponseDto> restaurantDtos = restaurants
            .stream()
            .map(r -> RestaurantResponseDto.builder()
                .id(r.getId())
                .name(r.getName())
                .city(r.getCity())
                .build())
            .toList();

    List<MenuItemResponseDto> menuItemDtos = items
            .stream()
            .map(m -> MenuItemResponseDto.builder()
                .id(m.getId())
                .name(m.getName())
                .price(m.getPrice())
                .restaurantName(m.getRestaurant().getName())
                    .build())
            .toList();

    return SearchResponseDto.builder()
            .restaurants(restaurantDtos)
            .menuItems(menuItemDtos)
            .build();
    }
}
