package com.siteshkumar.zomato_clone_backend.service;

import com.siteshkumar.zomato_clone_backend.entity.PaymentEntity;

public interface PaymentService {
    PaymentEntity createPayment(Long orderId);
    PaymentEntity getPaymentByOrderId(Long orderId);
    void markPaymentFailed(Long orderId);
}
