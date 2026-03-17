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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final PaymentMapper paymentMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PaymentResponseDto processPayment(Long orderId, String paymentMode, String key) {

        log.info("Processing payment. OrderId: {}, PaymentMode: {}, IdempotencyKey: {}", 
                    orderId, paymentMode, key);

        PaymentResponseDto stored = getStoredResponse(key);
        if (stored != null) {
            log.info("Returning stored payment response for idempotencyKey: {}", key);
            return stored;
        }

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found for payment. OrderId: {}", orderId);
                    return new RuntimeException("Order not found");
                });

        PaymentEntity existingPayment = paymentRepository
                .findByOrderId(orderId)
                .orElse(null);

        if (existingPayment != null) {
            log.info("Existing payment found. OrderId: {}, Status: {}", 
                        orderId, existingPayment.getStatus());

            if (existingPayment.getStatus() == PaymentStatus.SUCCESS) {
                log.warn("Payment already completed. OrderId: {}", orderId);
                throw new RuntimeException("Payment already completed");
            }
        }

        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setPaymentMode(paymentMode);
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setAmount(order.getTotalAmount());

        log.info("Calling payment gateway for OrderId: {}", orderId);

        boolean success = callPaymentGateway();

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(UUID.randomUUID().toString());
            log.info("Payment successful. OrderId: {}", orderId);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            log.warn("Payment failed. OrderId: {}", orderId);
        }

        PaymentEntity savedPayment = paymentRepository.save(payment);

        PaymentResponseDto response = paymentMapper.toDto(savedPayment);

        storeIdempotency(key, orderId, response);

        log.info("Payment processed and stored successfully. OrderId: {}", orderId);

        return response;
    }

    @Override
    @Transactional
    public PaymentResponseDto retryPayment(Long orderId, String key) {

        log.info("Retrying payment. OrderId: {}, IdempotencyKey: {}", orderId, key);

        PaymentResponseDto stored = getStoredResponse(key);
        if (stored != null) {
            log.info("Returning stored retry response for idempotencyKey: {}", key);
            return stored;
        }

        PaymentEntity payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("Payment not found for retry. OrderId: {}", orderId);
                    return new RuntimeException("Payment not found");
                });

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.warn("Retry attempted on already successful payment. OrderId: {}", orderId);
            throw new RuntimeException("Payment already successful");
        }

        payment.setStatus(PaymentStatus.PROCESSING);

        log.info("Calling payment gateway (retry). OrderId: {}", orderId);

        boolean success = callPaymentGateway();

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(UUID.randomUUID().toString());
            log.info("Payment retry successful. OrderId: {}", orderId);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            log.warn("Payment retry failed. OrderId: {}", orderId);
        }

        PaymentEntity savedPayment = paymentRepository.save(payment);

        PaymentResponseDto response = paymentMapper.toDto(savedPayment);

        storeIdempotency(key, orderId, response);

        log.info("Retry payment processed and stored. OrderId: {}", orderId);

        return response;
    }

    private boolean callPaymentGateway() {
        log.debug("Simulating payment gateway call...");
        return Math.random() > 0.3;
    }

    private PaymentResponseDto getStoredResponse(String key) {

        log.debug("Checking idempotency key: {}", key);

        Optional<IdempotencyKeyEntity> existingKey = idempotencyKeyRepository.findByIdempotencyKey(key);

        if (existingKey.isPresent()) {
            try {
                log.info("Idempotency hit for key: {}", key);
                return objectMapper.readValue(
                        existingKey.get().getResponseBody(),
                        PaymentResponseDto.class);
            } catch (Exception e) {
                log.error("Failed to parse stored response for key: {}", key, e);
                throw new RuntimeException("Failed to parse stored response");
            }
        }

        return null;
    }

    private void storeIdempotency(String key, Long orderId, PaymentResponseDto response) {
        try {
            log.debug("Storing idempotency response. Key: {}, OrderId: {}", key, orderId);

            IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
            entity.setIdempotencyKey(key);
            entity.setRequestPath("/payments/" + orderId);
            entity.setResponseBody(objectMapper.writeValueAsString(response));

            idempotencyKeyRepository.save(entity);

        } catch (Exception e) {
            log.error("Failed to store idempotency response. Key: {}", key, e);
            throw new RuntimeException("Failed to store idempotency response");
        }
    }
}