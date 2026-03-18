package com.siteshkumar.zomato_clone_backend.service;

import com.siteshkumar.zomato_clone_backend.dto.payment.PaymentIntentResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.payment.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto processPayment(Long orderId, String paymentMode, String key);
    PaymentResponseDto retryPayment(Long orderId, String key);
    PaymentIntentResponseDto createPaymentIntent(Long orderId, String key);
}
