package com.siteshkumar.zomato_clone_backend.utils;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.siteshkumar.zomato_clone_backend.entity.CartEntity;
import com.siteshkumar.zomato_clone_backend.entity.CartItemEntity;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CartUtils {

    public void recalculateCart(CartEntity cart){

        log.debug("Recalculating cart totals");

        BigDecimal total = BigDecimal.ZERO;
        int totalItems = 0;

        for(CartItemEntity item : cart.getCartItems()){
            total = total.add(item.getSubTotal());
            totalItems += item.getQuantity();
        }

        cart.setTotalAmount(total);
        cart.setTotalItems(totalItems);

        log.debug("Cart recalculated. TotalAmount: {}, TotalItems: {}", total, totalItems);
    }
}