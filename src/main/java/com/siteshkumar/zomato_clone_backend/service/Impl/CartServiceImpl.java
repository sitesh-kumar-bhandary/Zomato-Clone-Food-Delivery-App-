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
    public CartSummaryResponseDto createCart() {

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        log.info("Create cart request received for userId: {}", user.getId());

        if (user.getRole() != Role.CUSTOMER) {
            log.warn("Unauthorized cart creation attempt by userId: {}", user.getId());
            throw new AccessDeniedException("You are not allowed to do this");
        }

        if (cartRepository.findByUserId(user.getId()).isPresent()) {
            log.warn("Cart already exists for userId: {}", user.getId());
            throw new ConflictException("Cart already exists");
        }

        CartEntity cart = new CartEntity();

        cart.setUser(user);
        cart.setCartItems(new HashSet<>());
        cart.setTotalItems(0);
        cart.setTotalAmount(BigDecimal.ZERO);

        CartEntity savedCart = cartRepository.save(cart);

        log.info("Cart created successfully for userId: {}", user.getId());

        return cartMapper.toCartSummaryDto(savedCart);
    }

    @Override
    @Transactional
    public CartSummaryResponseDto addItem(AddCartItemRequestDto request) {

        log.info("Add item request received. MenuItemId: {}, Quantity: {}",
                request.getMenuItemId(),
                request.getQuantity());

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        CartEntity cart = cartRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.error("Cart not found for userId: {}", user.getId());
                    return new ResourceNotFoundException("Cart not found");
                });

        MenuItemEntity menuItem = menuItemRepository
                .findById(request.getMenuItemId())
                .orElseThrow(() -> {
                    log.error("Menu item not found with id: {}", request.getMenuItemId());
                    return new ResourceNotFoundException("Menu item not found");
                });

        // First item added in cart
        if (cart.getRestaurant() == null) {

            cart.setRestaurant(menuItem.getRestaurant());

            log.info("Restaurant assigned to cart. RestaurantId: {}",
                    menuItem.getRestaurant().getId());
        }

        // Different restaurant validation
        else if (!cart.getRestaurant().getId().equals(menuItem.getRestaurant().getId())) {

            log.warn("Attempt to add items from different restaurants. UserId: {}",
                    user.getId());

            throw new ConflictException(
                    "Can not add items from different restaurants");
        }

        Optional<CartItemEntity> existingItem = cart
                .getCartItems()
                .stream()
                .filter(item -> item.getMenuItem().getId().equals(menuItem.getId()))
                .findFirst();

        if (existingItem.isPresent()) {

            CartItemEntity item = existingItem.get();

            item.updateQuantity(item.getQuantity() + request.getQuantity());

            log.info("Existing cart item quantity updated. CartItemId: {}",
                    item.getId());
        }

        else {

            CartItemEntity cartItem = new CartItemEntity();

            cartItem.setCart(cart);
            cartItem.setMenuItem(menuItem);
            cartItem.setPriceAtTime(menuItem.getPrice());
            cartItem.updateQuantity(request.getQuantity());

            cart.getCartItems().add(cartItem);

            log.info("New item added to cart. MenuItemId: {}",
                    menuItem.getId());
        }

        cartUtils.recalculateCart(cart);

        CartEntity savedCart = cartRepository.save(cart);

        log.info("Cart updated successfully for userId: {}", user.getId());

        return cartMapper.toCartSummaryDto(savedCart);
    }

    @Override
    @Transactional
    public CartSummaryResponseDto updateItemQuantity(
            Long cartItemId,
            UpdateCartItemRequestDto request) {

        log.info(
                "Update cart item quantity request received. CartItemId: {}, Quantity: {}",
                cartItemId,
                request.getQuantity());

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        if (user.getRole() != Role.CUSTOMER) {

            log.warn(
                    "Unauthorized cart update attempt by userId: {}",
                    user.getId());

            throw new AccessDeniedException(
                    "You are not allowed to do this");
        }

        if (request.getQuantity() <= 0) {

            log.warn(
                    "Invalid quantity update request. Quantity: {}",
                    request.getQuantity());

            throw new ConflictException(
                    "Quantity must be greater than 0");
        }

        CartEntity cart = cartRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> {

                    log.error(
                            "Cart not found for userId: {}",
                            user.getId());

                    return new ResourceNotFoundException(
                            "Cart not found");
                });

        CartItemEntity cartItem = cart
                .getCartItems()
                .stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> {

                    log.error(
                            "Cart item not found with id: {}",
                            cartItemId);

                    return new ResourceNotFoundException(
                            "Cart item not found");
                });

        int oldQuantity = cartItem.getQuantity();

        cartItem.updateQuantity(request.getQuantity());

        log.info(
                "Cart item quantity updated successfully. CartItemId: {}, Old Quantity: {}, New Quantity: {}",
                cartItemId,
                oldQuantity,
                request.getQuantity());

        cartUtils.recalculateCart(cart);

        log.debug(
                "Cart recalculated successfully for userId: {}",
                user.getId());

        CartEntity savedCart = cartRepository.save(cart);

        log.info(
                "Cart saved successfully after quantity update for userId: {}",
                user.getId());

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