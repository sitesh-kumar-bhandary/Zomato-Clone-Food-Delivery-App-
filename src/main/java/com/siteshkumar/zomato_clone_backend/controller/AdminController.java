package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.siteshkumar.zomato_clone_backend.dto.admin.AdminReportSummaryDto;
import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import com.siteshkumar.zomato_clone_backend.service.AdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponseDto>> allOrders(
                                    @RequestParam(required = false) OrderStatus status, Pageable pageable){
        Page<OrderResponseDto> pages = adminService.allOrders(status, pageable);
        return ResponseEntity.ok(pages);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable Long id){
        adminService.blockUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable Long id){
        adminService.unblockUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restaurants/{id}/block")
    public ResponseEntity<Void> blockRestaurant(@PathVariable Long id){
        adminService.blockRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restaurants/{id}/unblock")
    public ResponseEntity<Void> unblockRestaurant(@PathVariable Long id){
        adminService.unblockRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports/summary")
    public ResponseEntity<AdminReportSummaryDto> getSummary(){
        AdminReportSummaryDto summary = adminService.getSummary();
        return ResponseEntity.ok(summary);
    }
}
