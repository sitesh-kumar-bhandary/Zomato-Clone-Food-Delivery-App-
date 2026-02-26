package com.siteshkumar.zomato_clone_backend.service;

import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.order.PlaceOrderRequestDto;

public interface  OrderService {

    OrderResponseDto placeOrder(PlaceOrderRequestDto request);
    
}
