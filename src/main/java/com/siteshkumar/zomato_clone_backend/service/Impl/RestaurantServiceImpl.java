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
import com.siteshkumar.zomato_clone_backend.enums.AccountStatus;
import com.siteshkumar.zomato_clone_backend.enums.Role;
import com.siteshkumar.zomato_clone_backend.exception.AccountNotApprovedException;
import com.siteshkumar.zomato_clone_backend.repository.RestaurantRepository;
import com.siteshkumar.zomato_clone_backend.security.CustomUserDetails;
import com.siteshkumar.zomato_clone_backend.service.RestaurantService;
import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService{

    private final AuthUtils authUtils;
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
        return new CreateRestaurantResponseDto(
            savedRestaurant.getId(),
            savedRestaurant.getName(),
            savedRestaurant.getCity(),
            savedRestaurant.isActive()
        );
    }

    @Override
    public UpdateRestaurantResponseDto updateRestaurant(Long id, UpdateRestaurantRequestDto request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateRestaurant'");
    }

    @Override
    public void deleteRestaurant(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRestaurant'");
    }

    @Override
    public Page<RestaurantResponseDto> getAllRestaurants(String city, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllRestaurants'");
    }

    @Override
    public RestaurantResponseDto getRestaurantById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRestaurantById'");
    }
}
