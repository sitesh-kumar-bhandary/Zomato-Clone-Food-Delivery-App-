package com.siteshkumar.zomato_clone_backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.siteshkumar.zomato_clone_backend.dto.admin.AdminReportSummaryDto;
import com.siteshkumar.zomato_clone_backend.dto.admin.UserApproveResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.order.OrderResponseDto;
import com.siteshkumar.zomato_clone_backend.enums.AccountStatus;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;

public interface AdminService {

    Page<OrderResponseDto> allOrders(OrderStatus status, Pageable pageable);
    void blockUser(Long id);
    void unblockUser(Long id);  
    AdminReportSummaryDto getSummary();
    UserApproveResponseDto approveUser(Long id);
    List<UserApproveResponseDto> getPendingUsers();

    void updateRestaurantStatus(Long id, AccountStatus status);
}
