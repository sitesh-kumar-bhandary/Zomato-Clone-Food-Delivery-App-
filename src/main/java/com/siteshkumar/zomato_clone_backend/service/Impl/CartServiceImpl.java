// package com.siteshkumar.zomato_clone_backend.service.Impl;

// import java.nio.file.AccessDeniedException;
// import java.util.HashSet;
// import java.util.Optional;

// import org.springframework.stereotype.Service;
// import com.siteshkumar.zomato_clone_backend.dto.cart.AddCartItemRequestDto;
// import com.siteshkumar.zomato_clone_backend.dto.cart.CartSummaryResponseDto;
// import com.siteshkumar.zomato_clone_backend.entity.CartEntity;
// import com.siteshkumar.zomato_clone_backend.entity.CartItemEntity;
// import com.siteshkumar.zomato_clone_backend.entity.MenuItemEntity;
// import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
// import com.siteshkumar.zomato_clone_backend.enums.Role;
// import com.siteshkumar.zomato_clone_backend.exception.ResourceNotFoundException;
// import com.siteshkumar.zomato_clone_backend.repository.CartRepository;
// import com.siteshkumar.zomato_clone_backend.repository.MenuItemRepository;
// import com.siteshkumar.zomato_clone_backend.service.CartService;
// import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class CartServiceImpl implements CartService {
//     private final MenuItemRepository menuItemRepository;
//     private final CartRepository cartRepository;
//     private final AuthUtils authUtils;

//     @Override
//     public CartSummaryResponseDto addItem(AddCartItemRequestDto request) {
//         MenuItemEntity menuItem = menuItemRepository.findById(request.getMenuItemId())
//                                 .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

//         UserEntity user = authUtils.getCurrentLoggedInUser().getUser();
//         if(user.getRole() != Role.CUSTOMER)
//             throw new AccessDeniedException("You are not allowed to do this");

//         Optional<CartEntity> optionalCart = cartRepository.findById(user.getId());

//         CartEntity cart;
//         if(optionalCart.isPresent())
//             cart = optionalCart.get();

//         else {
//             cart = new CartEntity();
//             cart.setUser(user);
//             cart.setRestaurant(menuItem.getRestaurant());
//             cart.setCartItems(new HashSet<>());
//         }



//         CartItemEntity cartItem = new CartItemEntity();
//         cartItem.setQuantity(request.getQuantity());
//         cartItem.setMenuItem(menuItem);
//         cartItem.setPriceAtTime(menuItem.getPrice());
//         updateQuantity(request.getQuantity());
//     }
// }
