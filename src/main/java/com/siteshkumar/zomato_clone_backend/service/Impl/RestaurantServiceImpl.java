package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siteshkumar.zomato_clone_backend.document.RestaurantDocument;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.CreateRestaurantRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.CreateRestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.RestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.UpdateRestaurantRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.UpdateRestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.RestaurantEntity;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
import com.siteshkumar.zomato_clone_backend.enums.AccountStatus;
import com.siteshkumar.zomato_clone_backend.enums.Role;
import com.siteshkumar.zomato_clone_backend.exception.AccountNotApprovedException;
import com.siteshkumar.zomato_clone_backend.exception.ResourceNotFoundException;
import com.siteshkumar.zomato_clone_backend.mapper.RestaurantMapper;
import com.siteshkumar.zomato_clone_backend.repository.elasticsearch.RestaurantSearchRepository;
import com.siteshkumar.zomato_clone_backend.repository.mysql.RestaurantRepository;
import com.siteshkumar.zomato_clone_backend.security.CustomUserDetails;
import com.siteshkumar.zomato_clone_backend.service.MetricsService;
import com.siteshkumar.zomato_clone_backend.service.RestaurantService;
import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantServiceImpl implements RestaurantService {

    private final AuthUtils authUtils;
    private final MetricsService metricsService;
    private final RestaurantMapper restaurantMapper;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantSearchRepository restaurantSearchRepository;

    @Override
    @Transactional
    public CreateRestaurantResponseDto createRestaurant(CreateRestaurantRequestDto request) {

        CustomUserDetails user = authUtils.getCurrentLoggedInUser();
        log.info("Create restaurant request by userId: {}", user.getUser().getId());

        if (user.getUser().getRole() != Role.RESTAURANT_OWNER) {
            log.warn("Unauthorized restaurant creation attempt. UserId: {}", user.getUser().getId());
            throw new AccessDeniedException("Only restaurant owners can create restaurant");
        }

        if (user.getUser().getStatus() != AccountStatus.APPROVED) {
            log.warn("Unapproved account tried to create restaurant. UserId: {}", user.getUser().getId());
            throw new AccountNotApprovedException("Account not approved yet");
        }

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName(request.getName());
        restaurant.setCity(request.getCity());
        restaurant.setActive(false);
        restaurant.setOwner(user.getUser());

        RestaurantEntity savedRestaurant = restaurantRepository.save(restaurant);

        RestaurantDocument document = restaurantMapper.toDocument(savedRestaurant);
        restaurantSearchRepository.save(document);

        log.info("Restaurant created successfully. RestaurantId: {}", savedRestaurant.getId());

        return restaurantMapper.toCreateResponseDto(savedRestaurant);
    }

    @Override
    @Transactional
    @CacheEvict(value = "restaurant", key = "#id")
    public UpdateRestaurantResponseDto updateRestaurant(Long id, UpdateRestaurantRequestDto request) {

        log.info("Updating restaurant. RestaurantId: {}", id);

        RestaurantEntity restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Restaurant not found. RestaurantId: {}", id);
                    return new ResourceNotFoundException("Restaurant with id " + id + " not found");
                });

        CustomUserDetails user = authUtils.getCurrentLoggedInUser();
        UserEntity currentUser = user.getUser();

        if (currentUser.getRole() != Role.ADMIN && !restaurant.getOwner().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized restaurant update attempt. UserId: {}, RestaurantId: {}",
                    currentUser.getId(), id);
            throw new AccessDeniedException("You cannot update this restaurant");
        }

        if (request.getName() != null && !request.getName().equals(restaurant.getName())) {
            log.debug("Updating restaurant name. RestaurantId: {}", id);
            restaurant.setName(request.getName());
        }

        RestaurantEntity updatedRestaurant = restaurantRepository.save(restaurant);

        RestaurantDocument document = restaurantMapper.toDocument(updatedRestaurant);
        restaurantSearchRepository.save(document);

        log.info("Restaurant updated successfully. RestaurantId: {}", id);

        return restaurantMapper.toUpdateResponseDto(updatedRestaurant);
    }

    @Override
    @Transactional
    @CacheEvict(value = "restaurant", key = "#id")
    public void deleteRestaurant(Long id) {

        log.info("Deleting restaurant (soft delete). RestaurantId: {}", id);

        RestaurantEntity restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Restaurant not found for deletion. RestaurantId: {}", id);
                    return new ResourceNotFoundException("Restaurant with id " + id + " not found");
                });

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        if (user.getRole() != Role.ADMIN) {
            log.warn("Unauthorized restaurant delete attempt. UserId: {}, RestaurantId: {}",
                    user.getId(), id);
            throw new AccessDeniedException("You are not allowed to delete this restaurant");
        }

        restaurant.setActive(false);
        restaurantRepository.save(restaurant);

        RestaurantDocument document = restaurantMapper.toDocument(restaurant);
        restaurantSearchRepository.save(document);

        log.info("Restaurant soft deleted successfully. RestaurantId: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RestaurantResponseDto> getAllRestaurants(String city, Pageable pageable) {

        log.info("Fetching restaurants. City: {}", city);

        Page<RestaurantEntity> restaurantPages;

        if (city == null || city.trim().isEmpty()) {
            log.info("No city provided. Fetching all restaurants");

            restaurantPages = restaurantRepository
                    .findByActiveTrueAndBlockedFalse(pageable);

        } else {
            restaurantPages = restaurantRepository
                    .findByCityIgnoreCaseAndActiveTrueAndBlockedFalse(city, pageable);
        }

        log.info("Restaurants fetched. Count: {}", restaurantPages.getTotalElements());

        return restaurantPages.map(restaurantMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "restaurant", key = "#id")
    public RestaurantResponseDto getRestaurantById(Long id) {

        log.info("Fetching restaurant (possibly from cache). RestaurantId: {}", id);

        metricsService.incrementDbHits();

        RestaurantEntity restaurant = restaurantRepository.findByIdAndBlockedFalse(id)
                .orElseThrow(() -> {
                    log.error("Restaurant not found. RestaurantId: {}", id);
                    return new ResourceNotFoundException("Restaurant with id " + id + " not found");
                });

        log.info("Restaurant fetched from database successfully. RestaurantId: {}", id);

        return restaurantMapper.toResponseDto(restaurant);
    }
}