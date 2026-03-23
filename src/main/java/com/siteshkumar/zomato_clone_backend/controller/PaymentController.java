package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.siteshkumar.zomato_clone_backend.dto.payment.PaymentIntentResponseDto;
import com.siteshkumar.zomato_clone_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}/intent")
    public ResponseEntity<PaymentIntentResponseDto> createPaymentIntent(
            @PathVariable Long orderId,
            @RequestHeader("Idempotency-Key") String key) {

        PaymentIntentResponseDto response = paymentService.createPaymentIntent(orderId, key);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/retry")
    public ResponseEntity<PaymentIntentResponseDto> retryPayment(
            @PathVariable Long orderId,
            @RequestHeader("Idempotency-Key") String key) {

        PaymentIntentResponseDto response = paymentService.retryPayment(orderId, key);
        return ResponseEntity.ok(response);
    }
}