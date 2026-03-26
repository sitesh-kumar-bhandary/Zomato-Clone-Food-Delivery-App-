package com.siteshkumar.zomato_clone_backend.service.Impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.order.PlaceOrderRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.order.UpdateOrderStatusRequestDto;
import com.siteshkumar.zomato_clone_backend.entity.*;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import com.siteshkumar.zomato_clone_backend.enums.PaymentStatus;
import com.siteshkumar.zomato_clone_backend.enums.Role;
import com.siteshkumar.zomato_clone_backend.exception.AddressNotFoundException;
import com.siteshkumar.zomato_clone_backend.exception.ResourceNotFoundException;
import com.siteshkumar.zomato_clone_backend.mapper.OrderMapper;
import com.siteshkumar.zomato_clone_backend.repository.*;
import com.siteshkumar.zomato_clone_backend.service.InventoryService;
import com.siteshkumar.zomato_clone_backend.service.OrderService;
import com.siteshkumar.zomato_clone_backend.service.redis.RedisLockService;
import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;
import com.siteshkumar.zomato_clone_backend.utils.CartUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final AuthUtils authUtils;
    private final CartUtils cartUtils;
    private final OrderMapper orderMapper;
    private final RedisLockService redisLockService;
    private final InventoryService inventoryService;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(PlaceOrderRequestDto request) {

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();
        log.info("Placing order for userId: {}", user.getId());

        if (user.isBlocked()) {
            log.warn("Blocked user attempted to place order. UserId: {}", user.getId());
            throw new AccessDeniedException("Your account is blocked by admin");
        }

        CartEntity cart = cartRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.error("Cart not found for userId: {}", user.getId());
                    return new ResourceNotFoundException("Cart not found");
                });

        if (cart.getCartItems().isEmpty()) {
            log.warn("Cart is empty for userId: {}", user.getId());
            throw new IllegalStateException("Cart is empty");
        }

        if (cart.getRestaurant() == null) {
            log.error("Cart restaurant is null for userId: {}", user.getId());
            throw new IllegalStateException("Cart restaurant is empty");
        }

        if (cart.getRestaurant().isBlocked()) {
            log.warn("Blocked restaurant order attempt. RestaurantId: {}", cart.getRestaurant().getId());
            throw new AccessDeniedException("Restaurant is blocked by admin");
        }

        if (!cart.getRestaurant().isActive()) {
            log.warn("Inactive restaurant order attempt. RestaurantId: {}", cart.getRestaurant().getId());
            throw new IllegalStateException("Restaurant is currently closed");
        }

        AddressEntity address = addressRepository
                .findById(request.getAddressId())
                .orElseThrow(() -> {
                    log.error("Address not found. AddressId: {}", request.getAddressId());
                    return new AddressNotFoundException("Please add the address first to place the order");
                });

        if (!address.getUser().getId().equals(user.getId())) {
            log.warn("Address does not belong to user. UserId: {}, AddressId: {}", user.getId(),
                    request.getAddressId());
            throw new AccessDeniedException("Address does not belong to current user");
        }

        cartUtils.recalculateCart(cart);
        log.debug("Cart recalculated for userId: {}", user.getId());

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setRestaurant(cart.getRestaurant());
        order.setTotalAmount(cart.getTotalAmount());

        AddressDetails source = address.getAddressDetails();
        AddressDetails snapshot = new AddressDetails();
        snapshot.setLabel(source.getLabel());
        snapshot.setStreet(source.getStreet());
        snapshot.setArea(source.getArea());
        snapshot.setCity(source.getCity());
        snapshot.setState(source.getState());
        snapshot.setPincode(source.getPincode());

        order.setDeliveryDetails(snapshot);

        List<CartItemEntity> items = cart.getCartItems()
                .stream()
                .sorted(Comparator.comparing(i -> i.getMenuItem().getId()))
                .toList();

        List<String> lockKeys = new ArrayList<>();
        Map<String, String> lockValues = new HashMap<>();

        try {
            for (CartItemEntity item : items) {
                Long menuItemId = item.getMenuItem().getId();
                String lockKey = "lock:menu:" + menuItemId;

                log.debug("Acquiring lock for menuItemId: {}", menuItemId);

                String lockValue = redisLockService.acquireLock(lockKey, 10000);

                if (lockValue == null) {
                    log.warn("Failed to acquire lock for menuItemId: {}", menuItemId);
                    throw new IllegalStateException("Menu item is currently being ordered. Please try again.");
                }

                lockKeys.add(lockKey);
                lockValues.put(lockKey, lockValue);
            }

            for (CartItemEntity item : items) {
                log.debug("Deducting stock for menuItemId: {}", item.getMenuItem().getId());
                inventoryService.deductStock(
                        item.getMenuItem().getId(),
                        item.getQuantity());
            }

        } finally {
            for (String key : lockKeys) {
                redisLockService.releaseLock(key, lockValues.get(key));
                log.debug("Released lock: {}", key);
            }
        }

        for (CartItemEntity cartItem : cart.getCartItems()) {
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setMenuItem(cartItem.getMenuItem());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtOrderTime(cartItem.getMenuItem().getPrice());

            order.addItem(orderItem);
        }

        OrderEntity savedOrder = orderRepository.save(order);
        log.info("Order placed successfully. OrderId: {}", savedOrder.getId());

        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setTotalItems(0);
        cartRepository.save(cart);

        log.info("Cart cleared after order. UserId: {}", user.getId());

        return orderMapper.toResponseDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getMyOrders(Pageable pageable) {

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();
        log.info("Fetching orders for userId: {}", user.getId());

        Page<OrderEntity> orderPages = orderRepository.findByUser_Id(user.getId(), pageable);

        return orderPages.map(orderMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getRestaurantOrders(Pageable pageable) {

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();
        log.info("Fetching restaurant orders for ownerId: {}", user.getId());

        if (user.isBlocked()) {
            log.warn("Blocked restaurant user tried to fetch orders. UserId: {}", user.getId());
            throw new AccessDeniedException("Your account is blocked by admin");
        }

        Page<OrderEntity> orderPage = orderRepository.findByRestaurant_Owner_Id(user.getId(), pageable);

        return orderPage.map(orderMapper::toResponseDto);
    }

    @Override
    @Transactional
    public OrderResponseDto cancelMyOrder(Long orderId) {

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();
        log.info("Cancel order request. OrderId: {}, UserId: {}", orderId, user.getId());

        if (user.isBlocked()) {
            log.warn("Blocked user tried to cancel order. UserId: {}", user.getId());
            throw new AccessDeniedException("Your account is blocked by admin");
        }

        OrderEntity order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found. OrderId: {}", orderId);
                    return new ResourceNotFoundException("Order not found");
                });

        boolean isCustomer = user.getRole() == Role.CUSTOMER && order.getUser().getId().equals(user.getId());
        boolean isRestaurant = user.getRole() == Role.RESTAURANT
                && order.getRestaurant().getOwner().getId().equals(user.getId());

        if (!isCustomer && !isRestaurant) {
            log.warn("Unauthorized order cancellation attempt. UserId: {}, OrderId: {}", user.getId(), orderId);
            throw new AccessDeniedException("You are not allowed to cancel this order");
        }

        if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY ||
                order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELLED) {
            log.warn("Invalid cancellation attempt for orderId: {} with status: {}", orderId, order.getStatus());
            throw new IllegalStateException("Order can not be cancelled at this stage");
        }

        for (OrderItemEntity item : order.getItems()) {
            inventoryService.restoreStock(
                    item.getMenuItem().getId(),
                    item.getQuantity());
        }

        order.updateStatus(OrderStatus.CANCELLED);

        log.info("Order cancelled successfully. OrderId: {}", orderId);

        return orderMapper.toResponseDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto request) {

        log.info("Updating order status. OrderId: {}, NewStatus: {}", orderId, request.getStatus());

        if (request.getStatus() == OrderStatus.CANCELLED)
            throw new IllegalArgumentException("Use cancel api");

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        if (user.isBlocked()) {
            log.warn("Blocked user tried to update order. UserId: {}", user.getId());
            throw new AccessDeniedException("Your account is blocked by admin");
        }

        OrderEntity order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found. OrderId: {}", orderId);
                    return new ResourceNotFoundException("Order not found");
                });

        if (!order.getRestaurant().getOwner().getId().equals(user.getId())) {
            log.warn("Unauthorized order status update. UserId: {}, OrderId: {}", user.getId(), orderId);
            throw new AccessDeniedException("You are not allowed to update this order");
        }

        order.updateStatus(request.getStatus());

        OrderEntity updatedOrder = orderRepository.save(order);

        log.info("Order status updated successfully. OrderId: {}", orderId);

        return orderMapper.toResponseDto(updatedOrder);
    }

    @Transactional
    public void markPaymentSuccess(Long orderId, String paymentIntentId) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        order.markPaymentSuccess(paymentIntentId);

        order.updateStatus(OrderStatus.CONFIRMED);

        log.info("Payment successful. OrderId: {}", orderId);

        orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(OrderEntity order) {

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Delivered order cannot be cancelled");
        }

        for (OrderItemEntity item : order.getItems()) {
            inventoryService.restoreStock(
                    item.getMenuItem().getId(),
                    item.getQuantity());
        }

        order.updateStatus(OrderStatus.CANCELLED);

        log.warn("Order cancelled by system. OrderId: {}", order.getId());
    }

    @Transactional
    public void handlePaymentTimeout(Long orderId) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getPaymentStatus() != PaymentStatus.PENDING) {
            return;
        }

        order.markPaymentTimeout();

        cancelOrder(order); 
    }

}