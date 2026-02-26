package com.siteshkumar.zomato_clone_backend.mapper;

import org.springframework.stereotype.Component;
import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.OrderEntity;

@Component
public class OrderMapper {
    public OrderResponseDto toResponseDto (OrderEntity entity){

        return new OrderResponseDto(
            entity.getId(),
            entity.getStatus().name(),
            entity.getTotalAmount()
        );
    }
}
