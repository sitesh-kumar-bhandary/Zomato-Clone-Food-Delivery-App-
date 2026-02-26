package com.siteshkumar.zomato_clone_backend.service.Impl;

import java.math.BigDecimal;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.order.PlaceOrderRequestDto;
import com.siteshkumar.zomato_clone_backend.entity.AddressDetails;
import com.siteshkumar.zomato_clone_backend.entity.AddressEntity;
import com.siteshkumar.zomato_clone_backend.entity.CartEntity;
import com.siteshkumar.zomato_clone_backend.entity.CartItemEntity;
import com.siteshkumar.zomato_clone_backend.entity.OrderEntity;
import com.siteshkumar.zomato_clone_backend.entity.OrderItemEntity;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import com.siteshkumar.zomato_clone_backend.exception.AddressNotFoundException;
import com.siteshkumar.zomato_clone_backend.exception.ResourceNotFoundException;
import com.siteshkumar.zomato_clone_backend.mapper.OrderMapper;
import com.siteshkumar.zomato_clone_backend.repository.AddressRepository;
import com.siteshkumar.zomato_clone_backend.repository.CartRepository;
import com.siteshkumar.zomato_clone_backend.repository.OrderRepository;
import com.siteshkumar.zomato_clone_backend.service.OrderService;
import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;
import com.siteshkumar.zomato_clone_backend.utils.CartUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AuthUtils authUtils;
    private final CartUtils cartUtils;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;

    @Override
    public OrderResponseDto placeOrder(PlaceOrderRequestDto request) {
        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        CartEntity cart = cartRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if(cart.getCartItems().isEmpty())
            throw new IllegalStateException("Cart is empty");

        if(cart.getRestaurant() == null)
            throw new IllegalStateException("Cart restaurant is empty");

        AddressEntity address = addressRepository
                            .findById(request.getAddressId())
                            .orElseThrow(() -> new AddressNotFoundException("Please add the address first to place the order"));

        if(! address.getUser().getId().equals(user.getId()))
            throw new AccessDeniedException("Address does not belong to current user");

        cartUtils.recalculateCart(cart);

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setRestaurant(cart.getRestaurant());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(cart.getTotalAmount());

        // Saving address snapshot
        AddressDetails source = address.getAddressDetails();

        AddressDetails snapshot = new AddressDetails();
        snapshot.setLabel(source.getLabel());
        snapshot.setStreet(source.getStreet());
        snapshot.setArea(source.getArea());
        snapshot.setCity(source.getCity());
        snapshot.setState(source.getState());
        snapshot.setPincode(source.getPincode());

        order.setDeliveryDetails(snapshot);

        // Converting cart item --> order item
        for (CartItemEntity cartItem : cart.getCartItems()) {

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setMenuItem(cartItem.getMenuItem());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtOrderTime(cartItem.getMenuItem().getPrice());

            order.addItem(orderItem);
        }

        OrderEntity savedOrder = orderRepository.save(order);

        // Clear cart
        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setTotalItems(0);
        cartRepository.save(cart);

        return orderMapper.toResponseDto(savedOrder);
    }
}
