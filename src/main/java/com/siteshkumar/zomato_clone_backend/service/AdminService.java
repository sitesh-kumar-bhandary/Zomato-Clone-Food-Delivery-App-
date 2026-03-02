package com.siteshkumar.zomato_clone_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;

public interface AdminService {

    Page<OrderResponseDto> allOrders(OrderStatus status, Pageable pageable);
    
}
