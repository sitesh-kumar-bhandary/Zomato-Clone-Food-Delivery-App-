package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
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
import com.siteshkumar.zomato_clone_backend.repository.RestaurantRepository;
import com.siteshkumar.zomato_clone_backend.security.CustomUserDetails;
import com.siteshkumar.zomato_clone_backend.service.RestaurantService;
import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService{

    private final AuthUtils authUtils;
    private final RestaurantMapper restaurantMapper;
    private final RestaurantRepository restaurantRepository;

    @Override
    public CreateRestaurantResponseDto createRestaurant(CreateRestaurantRequestDto request) {
        CustomUserDetails user = authUtils.getCurrentLoggedInUser();

        if(user.getUser().getRole() != Role.RESTAURANT)
            throw new AccessDeniedException("Only restaurant owners can create restaurant");

        if(user.getUser().getStatus() != AccountStatus.APPROVED)
            throw new AccountNotApprovedException("Account not approved yet");

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName(request.getName());
        restaurant.setCity(request.getCity());

        restaurant.setActive(false);
        restaurant.setOwner(user.getUser());

        RestaurantEntity savedRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toCreateResponseDto(savedRestaurant);
    }

    @Override
    public UpdateRestaurantResponseDto updateRestaurant(Long id, UpdateRestaurantRequestDto request) {
        RestaurantEntity restaurant = restaurantRepository.findById(id)
                                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found"));

        CustomUserDetails user = authUtils.getCurrentLoggedInUser();
        UserEntity currentUser = user.getUser();

        if(currentUser.getRole() != Role.ADMIN && ! restaurant.getOwner().getId().equals(currentUser.getId()))
            throw new AccessDeniedException("You cannot update this restaurant");


        if(request.getName() != null && ! request.getName().equals(restaurant.getName()))
            restaurant.setName(request.getName());

        RestaurantEntity updatedRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toUpdateResponseDto(updatedRestaurant);
    }

    @Override
    public void deleteRestaurant(Long id) {
        RestaurantEntity restaurant = restaurantRepository.findById(id)
                                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found"));

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();
        if(user.getRole() != Role.ADMIN)
            throw new AccessDeniedException("You are not allowed to delete this restaurant");

        // Implementing soft delete
        restaurant.setActive(false);
        restaurantRepository.save(restaurant);
    }

    @Override
    public Page<RestaurantResponseDto> getAllRestaurants(String city, Pageable pageable) {
        if(city == null || city.trim().isEmpty())
            return Page.empty(pageable);

        Page<RestaurantEntity> restaurantPages = restaurantRepository.findByCityIgnoreCaseAndActiveTrue(city, pageable);

        return restaurantPages.map(restaurantMapper::toResponseDto);
    }

    @Override
    public RestaurantResponseDto getRestaurantById(Long id) {
        RestaurantEntity restaurant = restaurantRepository.findById(id)
                                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found"));

        return restaurantMapper.toResponseDto(restaurant);
    }
}
