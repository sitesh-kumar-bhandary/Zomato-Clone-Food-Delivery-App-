package com.siteshkumar.zomato_clone_backend.service;

import com.siteshkumar.zomato_clone_backend.dto.payment.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto createPayment(Long orderId);

    PaymentResponseDto getPaymentByOrderId(Long orderId);

    void markPaymentFailed(Long orderId);
}