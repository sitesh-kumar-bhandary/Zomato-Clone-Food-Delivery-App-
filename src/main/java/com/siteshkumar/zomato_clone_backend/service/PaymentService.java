package com.siteshkumar.zomato_clone_backend.service;

import com.siteshkumar.zomato_clone_backend.dto.payment.PaymentIntentResponseDto;

public interface PaymentService {
    PaymentIntentResponseDto createPaymentIntent(Long orderId, String idempotencyKey);
    PaymentIntentResponseDto retryPayment(Long orderId, String idempotencyKey);
}
