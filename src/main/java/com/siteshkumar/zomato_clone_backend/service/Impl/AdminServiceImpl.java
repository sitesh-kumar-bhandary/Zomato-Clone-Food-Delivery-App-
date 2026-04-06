package com.siteshkumar.zomato_clone_backend.service.Impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siteshkumar.zomato_clone_backend.dto.admin.AdminReportSummaryDto;
import com.siteshkumar.zomato_clone_backend.dto.admin.UserApproveResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.OrderEntity;
import com.siteshkumar.zomato_clone_backend.entity.RestaurantEntity;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
import com.siteshkumar.zomato_clone_backend.enums.AccountStatus;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import com.siteshkumar.zomato_clone_backend.enums.Role;
import com.siteshkumar.zomato_clone_backend.exception.ResourceNotFoundException;
import com.siteshkumar.zomato_clone_backend.exception.UserAlreadyBlockedException;
import com.siteshkumar.zomato_clone_backend.mapper.OrderMapper;
import com.siteshkumar.zomato_clone_backend.mapper.UserMapper;
import com.siteshkumar.zomato_clone_backend.repository.mysql.OrderRepository;
import com.siteshkumar.zomato_clone_backend.repository.mysql.RestaurantRepository;
import com.siteshkumar.zomato_clone_backend.repository.mysql.UserRepository;
import com.siteshkumar.zomato_clone_backend.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> allOrders(OrderStatus status, Pageable pageable) {

        log.info("Fetching all orders. Status filter: {}, Page: {}", status, pageable);

        Page<OrderEntity> orderPage;

        if (status != null) {
            log.info("Fetching orders with status: {}", status);
            orderPage = orderRepository.findByStatus(status, pageable);
        } else {
            log.info("Fetching all orders without status filter");
            orderPage = orderRepository.findAll(pageable);
        }

        log.info("Total orders fetched: {}", orderPage.getTotalElements());

        return orderPage.map(orderMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void blockUser(Long id) {

        log.info("Attempting to block user with id: {}", id);

        UserEntity user = userRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found");
                });

        if (user.getRole() == Role.ADMIN) {
            log.warn("Attempt to block ADMIN user with id: {}", id);
            throw new AccessDeniedException("Admin cannot be blocked");
        }

        if (user.isBlocked()) {
            log.warn("User already blocked with id: {}", id);
            throw new UserAlreadyBlockedException("User is already blocked");
        }

        user.setBlocked(true);
        userRepository.save(user);

        log.info("User successfully blocked with id: {}", id);
    }

    @Override
    @Transactional
    public void unblockUser(Long id) {

        log.info("Attempting to unblock user with id: {}", id);

        UserEntity user = userRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found");
                });

        if (!user.isBlocked()) {
            log.warn("User is not blocked with id: {}", id);
            throw new IllegalStateException("User is not blocked");
        }

        user.setBlocked(false);
        userRepository.save(user);

        log.info("User successfully unblocked with id: {}", id);
    }

    @Override
    public AdminReportSummaryDto getSummary() {

        log.info("Generating admin summary report");

        long totalOrders = orderRepository.count();
        log.info("Total orders count: {}", totalOrders);

        BigDecimal totalRevenue = orderRepository
                .findByStatus(OrderStatus.DELIVERED)
                .stream()
                .map(OrderEntity::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Total revenue from delivered orders: {}", totalRevenue);

        Map<String, Long> ordersByStatus = orderRepository
                .countOrdersGroupByStatus()
                .stream()
                .collect(Collectors.toMap(
                        row -> ((OrderStatus) row[0]).name(),
                        row -> (Long) row[1]));

        log.info("Orders grouped by status: {}", ordersByStatus);

        log.info("Admin summary generated successfully");

        return AdminReportSummaryDto.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .ordersByStatus(ordersByStatus)
                .build();
    }

    @Override
    @Transactional
    public UserApproveResponseDto approveUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getRole() != Role.RESTAURANT_OWNER)
            throw new RuntimeException("Only restaurant owners need approval");

        if (user.getStatus() == AccountStatus.APPROVED)
            throw new IllegalStateException("User already approved");

        user.setStatus(AccountStatus.APPROVED);
        UserEntity savedUser = userRepository.save(user);

        return userMapper.toResponseDto(savedUser);
    }

    @Override
    public List<UserApproveResponseDto> getPendingUsers() {
        List<UserEntity> pendingOwners = userRepository.findByRoleAndStatus(Role.RESTAURANT_OWNER,
                AccountStatus.PENDING);

        return pendingOwners
                .stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateRestaurantStatus(Long id, AccountStatus status) {

        log.info("Updating restaurant status. id: {}, status: {}", id, status);

        RestaurantEntity restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        restaurant.setRestaurantStatus(status);

        restaurantRepository.save(restaurant);

        log.info("Restaurant status updated successfully");
    }
}