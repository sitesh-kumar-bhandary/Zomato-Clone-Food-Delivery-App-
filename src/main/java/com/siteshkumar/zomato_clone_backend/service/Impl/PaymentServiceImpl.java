package com.siteshkumar.zomato_clone_backend.service.Impl;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.siteshkumar.zomato_clone_backend.dto.payment.PaymentResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.OrderEntity;
import com.siteshkumar.zomato_clone_backend.entity.PaymentEntity;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import com.siteshkumar.zomato_clone_backend.enums.PaymentMode;
import com.siteshkumar.zomato_clone_backend.enums.PaymentStatus;
import com.siteshkumar.zomato_clone_backend.mapper.PaymentMapper;
import com.siteshkumar.zomato_clone_backend.repository.mysql.OrderRepository;
import com.siteshkumar.zomato_clone_backend.repository.mysql.PaymentRepository;
import com.siteshkumar.zomato_clone_backend.service.OrderService;
import com.siteshkumar.zomato_clone_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponseDto createPayment(Long orderId) {

        log.info("Creating payment for OrderId: {}", orderId);

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.isPaid()) {
            throw new RuntimeException("Order already paid");
        }

        Optional<PaymentEntity> existingPaymentOpt = paymentRepository.findByOrderId(orderId);

        if (existingPaymentOpt.isPresent()) {
            return paymentMapper.toDto(existingPaymentOpt.get());
        }

        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentMode(PaymentMode.COD);

        paymentRepository.save(payment);

        order.markPaymentSuccess(null);
        order.updateStatus(OrderStatus.CONFIRMED);

        log.info("Payment created successfully for OrderId: {}", orderId);

        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentByOrderId(Long orderId) {

        PaymentEntity payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional
    public void markPaymentFailed(Long orderId) {

        log.warn("Marking payment FAILED for OrderId: {}", orderId);

        PaymentEntity payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.FAILED) {
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);

        OrderEntity order = payment.getOrder();
        order.markPaymentFailed();

        orderService.cancelOrder(order);

        log.warn("Payment marked FAILED and order cancelled. OrderId: {}", orderId);
    }
}