package com.siteshkumar.zomato_clone_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.siteshkumar.zomato_clone_backend.dto.cart.CartItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.cart.CartSummaryResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.CartEntity;
import com.siteshkumar.zomato_clone_backend.entity.CartItemEntity;

@Component
public class CartMapper {
    public CartSummaryResponseDto toCartSummaryDto(CartEntity entity) {
        List<CartItemResponseDto> items = entity.getCartItems().stream()
                                .map(this::toCartItemDto)
                                .collect(Collectors.toList());

        return new CartSummaryResponseDto(
            items,
            entity.getTotalAmount(),
            entity.getTotalItems()
        );
    }

    public CartItemResponseDto toCartItemDto(CartItemEntity entity) {
        return new CartItemResponseDto(
            entity.getId(),
            entity.getMenuItem().getName(),
            entity.getPriceAtTime(),
            entity.getQuantity(),
            entity.getSubTotal()
        );
    }
}