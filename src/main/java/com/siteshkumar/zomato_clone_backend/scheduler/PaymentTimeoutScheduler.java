package com.siteshkumar.zomato_clone_backend.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.siteshkumar.zomato_clone_backend.entity.OrderEntity;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import com.siteshkumar.zomato_clone_backend.enums.PaymentStatus;
import com.siteshkumar.zomato_clone_backend.repository.mysql.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTimeoutScheduler {

    private final OrderRepository orderRepository;

    @Scheduled(cron = "0 */1 * * * *")
    public void handlePaymentTimeouts() {

        log.info("Running payment timeout scheduler...");

        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(15);

        List<OrderEntity> expiredOrders = orderRepository.findByPaymentStatusAndCreatedAtBefore(
                PaymentStatus.PENDING,
                timeoutTime);

        for (OrderEntity order : expiredOrders) {

            log.warn(
                    "Payment timeout detected for orderId: {}",
                    order.getId());

            order.markPaymentFailed();

            order.updateStatus(OrderStatus.CANCELLED);

            orderRepository.save(order);

            log.info(
                    "Order cancelled due to payment timeout. OrderId: {}",
                    order.getId());
        }
    }
}