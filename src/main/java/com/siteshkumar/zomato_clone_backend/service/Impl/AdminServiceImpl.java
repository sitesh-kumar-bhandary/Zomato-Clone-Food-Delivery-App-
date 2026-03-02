package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.OrderEntity;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import com.siteshkumar.zomato_clone_backend.mapper.OrderMapper;
import com.siteshkumar.zomato_clone_backend.repository.OrderRepository;
import com.siteshkumar.zomato_clone_backend.service.AdminService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final OrderRepository orderRepository;
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
}
