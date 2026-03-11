package com.siteshkumar.zomato_clone_backend.service.Impl;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.siteshkumar.zomato_clone_backend.dto.PaymentResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.OrderEntity;
import com.siteshkumar.zomato_clone_backend.entity.PaymentEntity;
import com.siteshkumar.zomato_clone_backend.enums.PaymentStatus;
import com.siteshkumar.zomato_clone_backend.mapper.PaymentMapper;
import com.siteshkumar.zomato_clone_backend.repository.OrderRepository;
import com.siteshkumar.zomato_clone_backend.repository.PaymentRepository;
import com.siteshkumar.zomato_clone_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public PaymentResponseDto processPayment(Long orderId, String paymentMode){

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        PaymentEntity existingPayment = paymentRepository
                .findByOrderId(orderId)
                .orElse(null);

        if(existingPayment != null){

            if(existingPayment.getStatus() == PaymentStatus.SUCCESS){
                throw new RuntimeException("Payment already completed");
            }

            return paymentMapper.toDto(existingPayment);
        }

        PaymentEntity payment = new PaymentEntity();

        payment.setOrder(order);
        payment.setPaymentMode(paymentMode);
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setAmount(order.getTotalAmount());

        boolean success = callPaymentGateway();

        if(success){
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(UUID.randomUUID().toString());
        } 
        else{
            payment.setStatus(PaymentStatus.FAILED);
        }

        PaymentEntity savedPayment = paymentRepository.save(payment);

        return paymentMapper.toDto(savedPayment);
    }

    private boolean callPaymentGateway(){
        return Math.random() > 0.3;
    }

    public PaymentResponseDto retryPayment(Long orderId){
        PaymentEntity payment = paymentRepository
            .findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));

        if(payment.getStatus() == PaymentStatus.SUCCESS){
            throw new RuntimeException("Payment already successful");
        }

        payment.setStatus(PaymentStatus.PROCESSING);

        boolean success = callPaymentGateway();

        if(success){
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(UUID.randomUUID().toString());
        }
        else{
            payment.setStatus(PaymentStatus.FAILED);
        }

        PaymentEntity savedPayment = paymentRepository.save(payment);

        return paymentMapper.toDto(savedPayment);
    }
}