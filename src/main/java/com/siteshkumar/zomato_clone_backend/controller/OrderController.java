package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.order.PlaceOrderRequestDto;
import com.siteshkumar.zomato_clone_backend.dto.order.UpdateOrderStatusRequestDto;
import com.siteshkumar.zomato_clone_backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponseDto> placeOrder(@Valid @RequestBody PlaceOrderRequestDto request) {
        OrderResponseDto placedOrder = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(placedOrder);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderResponseDto>> getMyOrders(
        @PageableDefault(size=10, sort="createdAt", direction=Sort.Direction.DESC) Pageable pageable){
        Page<OrderResponseDto> page = orderService.getMyOrders(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/restaurant")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<Page<OrderResponseDto>> getRestaurantOrders(
        @PageableDefault(size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable){
            Page<OrderResponseDto> page = orderService.getRestaurantOrders(pageable);
            return ResponseEntity.ok(page);
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@PathVariable Long orderId, @Valid @RequestBody UpdateOrderStatusRequestDto request){
        OrderResponseDto updatedOrderStatus = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(updatedOrderStatus);
    }

    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT')")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable Long orderId){
        OrderResponseDto cancelledOrder = orderService.cancelMyOrder(orderId);
        return ResponseEntity.ok(cancelledOrder);
    }
}
