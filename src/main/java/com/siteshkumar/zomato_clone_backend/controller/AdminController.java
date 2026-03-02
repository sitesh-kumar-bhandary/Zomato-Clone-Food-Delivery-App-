package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import com.siteshkumar.zomato_clone_backend.service.AdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDto>> allOrders(
                                    @RequestParam(required = false) OrderStatus status, Pageable pageable){
        Page<OrderResponseDto> pages = adminService.allOrders(status, pageable);
        return ResponseEntity.ok(pages);
    }
}
