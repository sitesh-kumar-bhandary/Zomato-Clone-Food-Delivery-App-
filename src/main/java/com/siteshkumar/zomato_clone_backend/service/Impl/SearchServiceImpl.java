package com.siteshkumar.zomato_clone_backend.service.Impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.siteshkumar.zomato_clone_backend.document.MenuItemDocument;
import com.siteshkumar.zomato_clone_backend.document.RestaurantDocument;
import com.siteshkumar.zomato_clone_backend.dto.SearchResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.MenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.RestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.repository.elasticsearch.MenuItemSearchRepository;
import com.siteshkumar.zomato_clone_backend.repository.elasticsearch.RestaurantSearchRepository;
import com.siteshkumar.zomato_clone_backend.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final RestaurantSearchRepository restaurantSearchRepository;
    private final MenuItemSearchRepository menuItemSearchRepository;

    @Override
    @Transactional(readOnly = true)
    public SearchResponseDto search(String query, int page, int size) {

        log.info("Search request received. Query: '{}', Page: {}, Size: {}", query, page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<RestaurantDocument> restaurants = restaurantSearchRepository
                .findByNameContainingIgnoreCaseOrCityContainingIgnoreCase(query, query, pageable);

        Page<MenuItemDocument> items = menuItemSearchRepository
                .findByNameContainingIgnoreCase(query, pageable);

        log.info("Search results fetched. Restaurants: {}, MenuItems: {}",
                restaurants.getTotalElements(), items.getTotalElements());

        return mapToResponse(restaurants, items, page, size);
    }

    private SearchResponseDto mapToResponse(
            Page<RestaurantDocument> restaurants,
            Page<MenuItemDocument> items,
            int page,
            int size) {

        log.debug("Mapping search results to response DTO");

        List<RestaurantResponseDto> restaurantDtos = restaurants
                .getContent()
                .stream()
                .map(r -> RestaurantResponseDto.builder()
                        .id(Long.parseLong(r.getId()))
                        .name(r.getName())
                        .city(r.getCity())
                        .build())
                .toList();

        List<MenuItemResponseDto> menuItemDtos = items.getContent()
                .stream()
                .map((MenuItemDocument m) -> MenuItemResponseDto.builder()
                        .id(m.getId() != null ? Long.parseLong(m.getId()) : null)
                        .name(m.getName())
                        .price(m.getPrice() != null ? BigDecimal.valueOf(m.getPrice()) : null) // ✅ FIX
                        .restaurantName(null)
                        .build())
                .toList();

        return SearchResponseDto.builder()
                .restaurants(restaurantDtos)
                .menuItems(menuItemDtos)
                .page(page)
                .size(size)
                .totalRestaurants(restaurants.getTotalElements())
                .totalMenuItems(items.getTotalElements())
                .build();
    }
}