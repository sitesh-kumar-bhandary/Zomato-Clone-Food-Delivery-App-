package com.siteshkumar.zomato_clone_backend.service.Impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.siteshkumar.zomato_clone_backend.dto.cart.AddCartItemRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.cart.CartSummaryResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.cart.UpdateCartItemRequestDto;
import com.siteshkumar.zomato_clone_backend.entity.CartEntity;
import com.siteshkumar.zomato_clone_backend.entity.CartItemEntity;
import com.siteshkumar.zomato_clone_backend.entity.MenuItemEntity;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
import com.siteshkumar.zomato_clone_backend.enums.Role;
import com.siteshkumar.zomato_clone_backend.exception.ConflictException;
import com.siteshkumar.zomato_clone_backend.exception.ResourceNotFoundException;
import com.siteshkumar.zomato_clone_backend.mapper.CartMapper;
import com.siteshkumar.zomato_clone_backend.repository.CartRepository;
import com.siteshkumar.zomato_clone_backend.repository.MenuItemRepository;
import com.siteshkumar.zomato_clone_backend.service.CartService;
import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final MenuItemRepository menuItemRepository;
    private final CartRepository cartRepository;
    private final AuthUtils authUtils;
    private final CartMapper cartMapper;

    @Override
    public CartSummaryResponseDto addItem(AddCartItemRequestDto request) {
        MenuItemEntity menuItem = menuItemRepository.findById(request.getMenuItemId())
                                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();
        if(user.getRole() != Role.CUSTOMER)
            throw new AccessDeniedException("You are not allowed to do this");

        CartEntity cart = cartRepository
                        .findByUserId(user.getId())
                        .orElseGet(() -> {
                            CartEntity newCart = new CartEntity();
                            newCart.setUser(user);
                            newCart.setRestaurant(menuItem.getRestaurant());
                            newCart.setCartItems(new HashSet<>());
                            return newCart;
                        });

        if(cart.getRestaurant() != null && ! cart.getRestaurant().getId().equals(menuItem.getRestaurant().getId()))
            throw new ConflictException("Can not add items from different restaurants");

        Optional<CartItemEntity> existingItem = cart
                                                .getCartItems()
                                                .stream()
                                                .filter(item -> item.getMenuItem().getId().equals(menuItem.getId()))
                                                .findFirst();

        if(existingItem.isPresent()){
            CartItemEntity item = existingItem.get();
            item.updateQuantity(item.getQuantity() + request.getQuantity());
        }

        else {
            CartItemEntity cartItem = new CartItemEntity();
            cartItem.setCart(cart);
            cartItem.setMenuItem(menuItem);
            cartItem.setPriceAtTime(menuItem.getPrice());
            cartItem.updateQuantity(request.getQuantity());

            cart.getCartItems().add(cartItem);
        }

        recalculateCart(cart);

        CartEntity savedCart = cartRepository.save(cart);

        return cartMapper.toCartSummaryDto(savedCart);
    }

    private void recalculateCart(CartEntity cart){
        BigDecimal total = BigDecimal.ZERO;
        int totalItems = 0;

        for(CartItemEntity item : cart.getCartItems()){
            total = total.add(item.getSubTotal());
            totalItems += item.getQuantity();
        }

        cart.setTotalAmount(total);
        cart.setTotalItems(totalItems);
    }

    @Override
    public CartSummaryResponseDto updateItem(Long cartItemId, UpdateCartItemRequestDto request) {
        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        CartEntity cart = cartRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItemEntity cartItem = cart
                                .getCartItems()
                                .stream()
                                .filter(item -> item.getId().equals(cartItemId))
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cartItem.updateQuantity(request.getQuantity());

        recalculateCart(cart);

        CartEntity savedCart = cartRepository.save(cart);

        return cartMapper.toCartSummaryDto(savedCart);
    }

    @Override
    public CartSummaryResponseDto deleteItem(Long cartItemId) {
        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        CartEntity cart = cartRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItemEntity cartItem = cart
                                .getCartItems()
                                .stream()
                                .filter(item -> item.getId().equals(cartItemId))
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.getCartItems().remove(cartItem);

        recalculateCart(cart);

        CartEntity savedCart = cartRepository.save(cart);

        return cartMapper.toCartSummaryDto(savedCart);
    }

    @Override
    public CartSummaryResponseDto cartSummary() {
        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        CartEntity cart = cartRepository
                        .findByUserId(user.getId())
                        .orElseGet(() -> {
                            CartEntity newCart = new CartEntity();
                            newCart.setCartItems(new HashSet<>());
                            newCart.setTotalAmount(BigDecimal.ZERO);
                            newCart.setTotalItems(0);
                            return newCart;
                        });

        return cartMapper.toCartSummaryDto(cart);
    }    
}
