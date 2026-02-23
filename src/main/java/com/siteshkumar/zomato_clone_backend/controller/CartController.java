package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.siteshkumar.zomato_clone_backend.dto.cart.AddCartItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.cart.CartSummaryResponseDto;
import com.siteshkumar.zomato_clone_backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<CartSummaryResponseDto> addItem(@Valid @RequestBody AddCartItemRequestDto request){
        CartSummaryResponseDto cartSummary = cartService.addItem(request);
        return ResponseEntity.ok(cartSummary);
    }

    // public ResponseEntity<> updateCartItem(){

    // }

    // public ResponseEntity<> deleteCartItem(){

    // }

    // public ResponseEntity<> cartSummary(){

    // }
}
