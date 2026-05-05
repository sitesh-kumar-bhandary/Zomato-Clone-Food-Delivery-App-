package com.siteshkumar.zomato_clone_backend.controller;

import java.util.List;
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
import com.siteshkumar.zomato_clone_backend.dto.admin.UserApproveResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.RestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.enums.AccountStatus;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import com.siteshkumar.zomato_clone_backend.service.AdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // Admin analytics
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponseDto>> allOrders(
            @RequestParam(required = false) OrderStatus status, Pageable pageable) {
        Page<OrderResponseDto> pages = adminService.allOrders(status, pageable);
        return ResponseEntity.ok(pages);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports/summary")
    public ResponseEntity<AdminReportSummaryDto> getSummary() {
        AdminReportSummaryDto summary = adminService.getSummary();
        return ResponseEntity.ok(summary);
    }

    // For user access
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/pending")
    public ResponseEntity<List<UserApproveResponseDto>> getPendingUsers() {
        List<UserApproveResponseDto> users = adminService.getPendingUsers();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable Long id) {
        adminService.blockUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<Void> unblockUser(@PathVariable Long id) {
        adminService.unblockUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/approve")
    public ResponseEntity<UserApproveResponseDto> approveUser(@PathVariable Long id) {
        UserApproveResponseDto response = adminService.approveUser(id);
        return ResponseEntity.ok(response);
    }

    // For Restaurant access
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/restaurants/pending")
    public ResponseEntity<List<RestaurantResponseDto>> getPendingRestaurants() {
        List<RestaurantResponseDto> response = adminService.getPendingRestaurants();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restaurants/{id}/block")
    public ResponseEntity<RestaurantResponseDto> blockRestaurant(@PathVariable Long id){
        RestaurantResponseDto response = adminService.updateRestaurantStatus(id, AccountStatus.SUSPENDED);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restaurants/{id}/unblock")
    public ResponseEntity<RestaurantResponseDto> unblockRestaurant(@PathVariable Long id){
        RestaurantResponseDto response = adminService.updateRestaurantStatus(id, AccountStatus.APPROVED);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restaurants/{id}/approve")
    public ResponseEntity<RestaurantResponseDto> approveRestaurant(@PathVariable Long id){
        RestaurantResponseDto response = adminService.updateRestaurantStatus(id, AccountStatus.APPROVED);
        return ResponseEntity.ok(response);
    }
}
