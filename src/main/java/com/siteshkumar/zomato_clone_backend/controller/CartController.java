package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartSummaryResponseDto> createCart() {
        CartSummaryResponseDto cart = cartService.createCart();
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartSummaryResponseDto> addItem(@Valid @RequestBody AddCartItemRequestDto request){
        CartSummaryResponseDto cartSummary = cartService.addItem(request);
        return ResponseEntity.ok(cartSummary);
    }

    @PatchMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartSummaryResponseDto> updateParticularItemQuantity(@PathVariable Long cartItemId, @Valid @RequestBody UpdateCartItemRequestDto request) {

        CartSummaryResponseDto cart = cartService.updateItemQuantity(cartItemId, request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartSummaryResponseDto> deleteItem(@PathVariable Long cartItemId){
        CartSummaryResponseDto remainingCart = cartService.deleteItem(cartItemId);
        return ResponseEntity.ok(remainingCart);
    }

    @GetMapping("/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartSummaryResponseDto> cartSummary(){
        CartSummaryResponseDto cart = cartService.cartSummary();
        return ResponseEntity.ok(cart);
    }
}
