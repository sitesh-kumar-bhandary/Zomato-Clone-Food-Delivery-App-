package com.siteshkumar.zomato_clone_backend.service.Impl;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siteshkumar.zomato_clone_backend.dto.PaymentResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.IdempotencyKeyEntity;
import com.siteshkumar.zomato_clone_backend.entity.OrderEntity;
import com.siteshkumar.zomato_clone_backend.entity.PaymentEntity;
import com.siteshkumar.zomato_clone_backend.enums.PaymentStatus;
import com.siteshkumar.zomato_clone_backend.mapper.PaymentMapper;
import com.siteshkumar.zomato_clone_backend.repository.IdempotencyKeyRepository;
import com.siteshkumar.zomato_clone_backend.repository.OrderRepository;
import com.siteshkumar.zomato_clone_backend.repository.PaymentRepository;
import com.siteshkumar.zomato_clone_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final PaymentMapper paymentMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PaymentResponseDto processPayment(Long orderId, String paymentMode, String key) {

        PaymentResponseDto stored = getStoredResponse(key);
        if (stored != null)
            return stored;

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        PaymentEntity existingPayment = paymentRepository
                .findByOrderId(orderId)
                .orElse(null);

        if (existingPayment != null) {
            if (existingPayment.getStatus() == PaymentStatus.SUCCESS) {
                throw new RuntimeException("Payment already completed");
            }
        }

        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setPaymentMode(paymentMode);
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setAmount(order.getTotalAmount());

        boolean success = callPaymentGateway();

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(UUID.randomUUID().toString());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        PaymentEntity savedPayment = paymentRepository.save(payment);

        PaymentResponseDto response = paymentMapper.toDto(savedPayment);

        storeIdempotency(key, orderId, response);

        return response;
    }

    @Override
    @Transactional
    public PaymentResponseDto retryPayment(Long orderId, String key) {

        PaymentResponseDto stored = getStoredResponse(key);
        if (stored != null)
            return stored;

        PaymentEntity payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new RuntimeException("Payment already successful");
        }

        payment.setStatus(PaymentStatus.PROCESSING);

        boolean success = callPaymentGateway();

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(UUID.randomUUID().toString());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        PaymentEntity savedPayment = paymentRepository.save(payment);

        PaymentResponseDto response = paymentMapper.toDto(savedPayment);

        storeIdempotency(key, orderId, response);

        return response;
    }

    private boolean callPaymentGateway() {
        return Math.random() > 0.3;
    }

    private PaymentResponseDto getStoredResponse(String key) {
        Optional<IdempotencyKeyEntity> existingKey = idempotencyKeyRepository.findByIdempotencyKey(key);

        if (existingKey.isPresent()) {
            try {
                return objectMapper.readValue(
                        existingKey.get().getResponseBody(),
                        PaymentResponseDto.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse stored response");
            }
        }

        return null;
    }

    private void storeIdempotency(String key, Long orderId, PaymentResponseDto response) {
        try {
            IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
            entity.setIdempotencyKey(key);
            entity.setRequestPath("/payments/" + orderId);
            entity.setResponseBody(objectMapper.writeValueAsString(response));

            idempotencyKeyRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store idempotency response");
        }
    }
}