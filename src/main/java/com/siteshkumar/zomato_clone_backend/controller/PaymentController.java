package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.siteshkumar.zomato_clone_backend.entity.PaymentEntity;
import com.siteshkumar.zomato_clone_backend.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    public ResponseEntity<PaymentEntity> createPayment(@PathVariable Long orderId) {

        PaymentEntity payment = paymentService.createPayment(orderId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentEntity> getPayment(@PathVariable Long orderId) {

        PaymentEntity payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{orderId}/fail")
    public ResponseEntity<String> markPaymentFailed(@PathVariable Long orderId) {

        paymentService.markPaymentFailed(orderId);
        return ResponseEntity.ok("Payment marked as FAILED");
    }
}