package com.siteshkumar.zomato_clone_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.siteshkumar.zomato_clone_backend.dto.payment.PaymentResponseDto;
import com.siteshkumar.zomato_clone_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    public ResponseEntity<PaymentResponseDto> createPayment(@PathVariable Long orderId) {

        PaymentResponseDto payment = paymentService.createPayment(orderId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponseDto> getPayment(@PathVariable Long orderId) {

        PaymentResponseDto payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{orderId}/fail")
    public ResponseEntity<String> markPaymentFailed(@PathVariable Long orderId) {

        paymentService.markPaymentFailed(orderId);
        return ResponseEntity.ok("Payment marked as FAILED");
    }
}