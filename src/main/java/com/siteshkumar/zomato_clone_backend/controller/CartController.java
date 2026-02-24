package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.siteshkumar.zomato_clone_backend.dto.cart.AddCartItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.cart.CartSummaryResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.cart.UpdateCartItemRequestDto;
import com.siteshkumar.zomato_clone_backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartSummaryResponseDto> addItem(@Valid @RequestBody AddCartItemRequestDto request){
        CartSummaryResponseDto cartSummary = cartService.addItem(request);
        return ResponseEntity.ok(cartSummary);
    }

    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartSummaryResponseDto> updateItem(@PathVariable Long cartItemId, @Valid @RequestBody UpdateCartItemRequestDto request){
        CartSummaryResponseDto updatedCart = cartService.updateItem(cartItemId, request);
        return ResponseEntity.ok(updatedCart);
    }

    // public ResponseEntity<> deleteCartItem(){

    // }

    // public ResponseEntity<> cartSummary(){

    // }
}
