package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.OrderEntity;
import com.siteshkumar.zomato_clone_backend.entity.RestaurantEntity;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import com.siteshkumar.zomato_clone_backend.enums.Role;
import com.siteshkumar.zomato_clone_backend.exception.ResourceNotFoundException;
import com.siteshkumar.zomato_clone_backend.exception.UserAlreadyBlockedException;
import com.siteshkumar.zomato_clone_backend.mapper.OrderMapper;
import com.siteshkumar.zomato_clone_backend.repository.OrderRepository;
import com.siteshkumar.zomato_clone_backend.repository.RestaurantRepository;
import com.siteshkumar.zomato_clone_backend.repository.UserRepository;
import com.siteshkumar.zomato_clone_backend.service.AdminService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    public Page<OrderResponseDto> allOrders(OrderStatus status, Pageable pageable) {
        Page<OrderEntity> orderPage;

        if(status != null){
            orderPage = orderRepository.findByStatus(status, pageable);
        }

        else {
            orderPage = orderRepository.findAll(pageable);
        }

        return orderPage.map(orderMapper :: toResponseDto);
    }

    @Override
    public void blockUser(Long id) {
        UserEntity user = userRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(user.getRole() == Role.ADMIN)
            throw new AccessDeniedException("Admin cannot be blocked");

        if(user.isBlocked())
            throw new UserAlreadyBlockedException("User is already blocked");

        user.setBlocked(true);
        userRepository.save(user);
    }

    @Override
    public void unblockUser(Long id) {
        UserEntity user = userRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(! user.isBlocked())
            throw new IllegalStateException("User is not blocked");

        user.setBlocked(false);
        userRepository.save(user);
    }

    @Override
    public void blockRestaurant(Long id) {
        RestaurantEntity restaurant = restaurantRepository
                                .findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        if(restaurant.isBlocked())
            throw new IllegalStateException("Restaurant is already blocked");

        restaurant.setBlocked(true);
        restaurantRepository.save(restaurant);
    }

    @Override
    public void unblockRestaurant(Long id) {
        RestaurantEntity restaurant = restaurantRepository
                                .findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        if(! restaurant.isBlocked())
            throw new IllegalStateException("Restaurant is not blocked");

        restaurant.setBlocked(false);
        restaurantRepository.save(restaurant);
    }
}
