package com.siteshkumar.zomato_clone_backend.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.siteshkumar.zomato_clone_backend.repository.mysql.OrderRepository;
import com.siteshkumar.zomato_clone_backend.repository.mysql.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportScheduler {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 1 * * ?")            // It will run at 1AM everyday.
    public void generateDailyReport(){
        long totalOrders = orderRepository.count();
        long totalUsers = userRepository.count();
        Double totalRevenue = orderRepository.getTotalRevenue();

        log.info("""
                
                [DAILY REPORT]
                Orders   : {}
                Revenue  : ₹{}
                Users    : {}
                
                """, totalOrders, totalRevenue, totalUsers);
    }
}
