package com.siteshkumar.zomato_clone_backend.service;

import com.siteshkumar.zomato_clone_backend.dto.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto processPayment(Long orderId, String paymentMode, String key);
    PaymentResponseDto retryPayment(Long orderId, String key);
}
