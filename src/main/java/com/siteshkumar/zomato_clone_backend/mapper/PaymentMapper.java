package com.siteshkumar.zomato_clone_backend.mapper;

import org.springframework.stereotype.Component;
import com.siteshkumar.zomato_clone_backend.dto.payment.PaymentResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.PaymentEntity;

@Component
public class PaymentMapper {

    public PaymentResponseDto toDto(PaymentEntity entity) {

        return PaymentResponseDto.builder()
                .orderId(entity.getOrder().getId())
                .paymentMode(entity.getPaymentMode())
                .status(entity.getStatus())
                .amount(entity.getAmount())
                .build();
    }
}