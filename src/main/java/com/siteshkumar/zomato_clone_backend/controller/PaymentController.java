package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.siteshkumar.zomato_clone_backend.dto.PaymentResponseDto;
import com.siteshkumar.zomato_clone_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    public ResponseEntity<PaymentResponseDto> processPayment(
                                    @RequestHeader("Idempotency-Key") String key, 
                                    @PathVariable Long orderId, 
                                    @RequestParam String paymentMode) {

        PaymentResponseDto payment = paymentService.processPayment(orderId, paymentMode, key);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{orderId}/retry")
    public ResponseEntity<PaymentResponseDto> retryPayment(
                                    @PathVariable Long orderId, 
                                    @RequestHeader("Idempotency-Key") String key) {

        PaymentResponseDto payment = paymentService.retryPayment(orderId, key);
        return ResponseEntity.ok(payment);
    }
}
