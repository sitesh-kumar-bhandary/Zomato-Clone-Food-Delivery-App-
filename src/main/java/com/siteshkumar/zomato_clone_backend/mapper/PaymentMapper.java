package com.siteshkumar.zomato_clone_backend.mapper;

import org.springframework.stereotype.Component;

import com.siteshkumar.zomato_clone_backend.dto.payment.PaymentResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.PaymentEntity;

@Component
public class PaymentMapper {

    public PaymentResponseDto toDto(PaymentEntity payment){

        return PaymentResponseDto.builder()
            .paymentId(payment.getId())
            .orderId(payment.getOrder().getId())
            .paymentMode(payment.getPaymentMode())
            .transactionId(payment.getTransactionId())
            .status(payment.getStatus())
            .amount(payment.getAmount())
            .build();
    }
}
