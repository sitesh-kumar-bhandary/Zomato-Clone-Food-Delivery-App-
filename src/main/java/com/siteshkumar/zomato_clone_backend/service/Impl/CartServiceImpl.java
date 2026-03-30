package com.siteshkumar.zomato_clone_backend.service.Impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.siteshkumar.zomato_clone_backend.repository.mysql.CartRepository;
import com.siteshkumar.zomato_clone_backend.repository.mysql.MenuItemRepository;
import com.siteshkumar.zomato_clone_backend.service.CartService;
import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;
import com.siteshkumar.zomato_clone_backend.utils.CartUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final MenuItemRepository menuItemRepository;
    private final CartRepository cartRepository;
    private final CartUtils cartUtils;
    private final AuthUtils authUtils;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartSummaryResponseDto addItem(AddCartItemRequestDto request) {

        log.info("Add item to cart initiated. MenuItemId: {}, Quantity: {}", 
                    request.getMenuItemId(), request.getQuantity());

        MenuItemEntity menuItem = menuItemRepository.findById(request.getMenuItemId())
                                .orElseThrow(() -> {
                                    log.error("Menu item not found with id: {}", request.getMenuItemId());
                                    return new ResourceNotFoundException("Menu item not found");
                                });

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        if(user.getRole() != Role.CUSTOMER) {
            log.warn("Unauthorized cart access attempt by userId: {}", user.getId());
            throw new AccessDeniedException("You are not allowed to do this");
        }

        CartEntity cart = cartRepository
                        .findByUserId(user.getId())
                        .orElseGet(() -> {
                            log.info("No existing cart found. Creating new cart for userId: {}", user.getId());
                            CartEntity newCart = new CartEntity();
                            newCart.setUser(user);
                            newCart.setRestaurant(menuItem.getRestaurant());
                            newCart.setCartItems(new HashSet<>());
                            return newCart;
                        });

        if(cart.getRestaurant() != null && ! cart.getRestaurant().getId().equals(menuItem.getRestaurant().getId())) {
            log.warn("Attempt to add items from different restaurants. UserId: {}", user.getId());
            throw new ConflictException("Can not add items from different restaurants");
        }

        Optional<CartItemEntity> existingItem = cart
                                                .getCartItems()
                                                .stream()
                                                .filter(item -> item.getMenuItem().getId().equals(menuItem.getId()))
                                                .findFirst();

        if(existingItem.isPresent()){
            log.info("Updating existing cart item. MenuItemId: {}", menuItem.getId());
            CartItemEntity item = existingItem.get();
            item.updateQuantity(item.getQuantity() + request.getQuantity());
        }

        else {
            log.info("Adding new item to cart. MenuItemId: {}", menuItem.getId());

            CartItemEntity cartItem = new CartItemEntity();
            cartItem.setCart(cart);
            cartItem.setMenuItem(menuItem);
            cartItem.setPriceAtTime(menuItem.getPrice());
            cartItem.updateQuantity(request.getQuantity());

            cart.getCartItems().add(cartItem);
        }

        cartUtils.recalculateCart(cart);
        log.debug("Cart recalculated for userId: {}", user.getId());

        CartEntity savedCart = cartRepository.save(cart);

        log.info("Cart updated successfully for userId: {}", user.getId());

        return cartMapper.toCartSummaryDto(savedCart);
    }

    @Override
    @Transactional
    public CartSummaryResponseDto updateItem(Long cartItemId, UpdateCartItemRequestDto request) {

        log.info("Updating cart item. CartItemId: {}, New Quantity: {}", cartItemId, request.getQuantity());

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        CartEntity cart = cartRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() -> {
                            log.error("Cart not found for userId: {}", user.getId());
                            return new ResourceNotFoundException("Cart not found");
                        });

        CartItemEntity cartItem = cart
                                .getCartItems()
                                .stream()
                                .filter(item -> item.getId().equals(cartItemId))
                                .findFirst()
                                .orElseThrow(() -> {
                                    log.error("Cart item not found with id: {}", cartItemId);
                                    return new ResourceNotFoundException("Cart item not found");
                                });

        cartItem.updateQuantity(request.getQuantity());

        cartUtils.recalculateCart(cart);
        log.debug("Cart recalculated after update. UserId: {}", user.getId());

        CartEntity savedCart = cartRepository.save(cart);

        log.info("Cart item updated successfully. CartItemId: {}", cartItemId);

        return cartMapper.toCartSummaryDto(savedCart);
    }

    @Override
    @Transactional
    public CartSummaryResponseDto deleteItem(Long cartItemId) {

        log.info("Deleting cart item. CartItemId: {}", cartItemId);

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        CartEntity cart = cartRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() -> {
                            log.error("Cart not found for userId: {}", user.getId());
                            return new ResourceNotFoundException("Cart not found");
                        });

        CartItemEntity cartItem = cart
                                .getCartItems()
                                .stream()
                                .filter(item -> item.getId().equals(cartItemId))
                                .findFirst()
                                .orElseThrow(() -> {
                                    log.error("Cart item not found with id: {}", cartItemId);
                                    return new ResourceNotFoundException("Cart item not found");
                                });

        cart.getCartItems().remove(cartItem);

        cartUtils.recalculateCart(cart);
        log.debug("Cart recalculated after deletion. UserId: {}", user.getId());

        CartEntity savedCart = cartRepository.save(cart);

        log.info("Cart item deleted successfully. CartItemId: {}", cartItemId);

        return cartMapper.toCartSummaryDto(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartSummaryResponseDto cartSummary() {

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();
        log.info("Fetching cart summary for userId: {}", user.getId());

        CartEntity cart = cartRepository
                        .findByUserId(user.getId())
                        .orElseGet(() -> {
                            log.info("No cart found. Returning empty cart for userId: {}", user.getId());
                            CartEntity newCart = new CartEntity();
                            newCart.setCartItems(new HashSet<>());
                            newCart.setTotalAmount(BigDecimal.ZERO);
                            newCart.setTotalItems(0);
                            return newCart;
                        });

        log.info("Cart summary fetched successfully for userId: {}", user.getId());

        return cartMapper.toCartSummaryDto(cart);
    }    
}