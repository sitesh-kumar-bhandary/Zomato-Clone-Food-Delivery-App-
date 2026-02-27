package com.siteshkumar.zomato_clone_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.order.PlaceOrderRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.order.UpdateOrderStatusRequestDto;

public interface  OrderService {

    // Customer api
    OrderResponseDto placeOrder(PlaceOrderRequestDto request);
    Page<OrderResponseDto> getMyOrders(Pageable pageable);
    OrderResponseDto cancelMyOrder(Long orderId);

    // Restaurant api
    Page<OrderResponseDto> getRestaurantOrders(Pageable pageable); 
    OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto request);   
}
