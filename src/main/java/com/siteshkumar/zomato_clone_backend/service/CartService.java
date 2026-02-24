package com.siteshkumar.zomato_clone_backend.service;

import com.siteshkumar.zomato_clone_backend.dto.cart.AddCartItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.cart.CartSummaryResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.cart.UpdateCartItemRequestDto;

public interface CartService {
    CartSummaryResponseDto addItem(AddCartItemRequestDto request);
    CartSummaryResponseDto updateItem(Long cartItemId, UpdateCartItemRequestDto request);
    CartSummaryResponseDto deleteItem(Long cartItemId);
    CartSummaryResponseDto cartSummary();
}
