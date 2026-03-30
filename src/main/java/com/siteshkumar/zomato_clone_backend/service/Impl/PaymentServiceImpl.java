package com.siteshkumar.zomato_clone_backend.service.Impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siteshkumar.zomato_clone_backend.dto.payment.PaymentIntentResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.IdempotencyKeyEntity;
import com.siteshkumar.zomato_clone_backend.entity.OrderEntity;
import com.siteshkumar.zomato_clone_backend.entity.PaymentEntity;
import com.siteshkumar.zomato_clone_backend.entity.ProcessedWebhookEntity;
import com.siteshkumar.zomato_clone_backend.enums.OrderStatus;
import com.siteshkumar.zomato_clone_backend.enums.PaymentMode;
import com.siteshkumar.zomato_clone_backend.enums.PaymentStatus;
import com.siteshkumar.zomato_clone_backend.repository.mysql.IdempotencyKeyRepository;
import com.siteshkumar.zomato_clone_backend.repository.mysql.OrderRepository;
import com.siteshkumar.zomato_clone_backend.repository.mysql.PaymentRepository;
import com.siteshkumar.zomato_clone_backend.repository.mysql.ProcessedWebhookRepository;
import com.siteshkumar.zomato_clone_backend.service.OrderService;
import com.siteshkumar.zomato_clone_backend.service.PaymentService;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final ProcessedWebhookRepository processedWebhookRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PaymentIntentResponseDto createPaymentIntent(Long orderId, String key) {

        log.info("Creating payment intent. OrderId: {}, IdempotencyKey: {}", orderId, key);

        PaymentIntentResponseDto stored = getStoredIntentResponse(key);
        if (stored != null)
            return stored;

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.isPaid()) {
            throw new RuntimeException("Order already paid");
        }

        Optional<PaymentEntity> existingPaymentOpt = paymentRepository.findByOrderId(orderId);

        if (existingPaymentOpt.isPresent()) {
            PaymentEntity existing = existingPaymentOpt.get();

            if (existing.getStatus() == PaymentStatus.SUCCESS) {
                throw new RuntimeException("Order already paid");
            }

            if (existing.getStatus() == PaymentStatus.PENDING) {
                log.info("Reusing existing PaymentIntent");

                return new PaymentIntentResponseDto(
                        orderId,
                        existing.getStripePaymentIntentId(),
                        existing.getClientSecret(),
                        PaymentStatus.PENDING,
                        existing.getAmount());
            }
        }

        try {
            Long amountInPaise = order.getTotalAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInPaise)
                    .setCurrency("inr")
                    .putMetadata("orderId", orderId.toString())
                    .build();

            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(key)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params, options);

            PaymentEntity payment = new PaymentEntity();
            payment.setOrder(order);
            payment.setStatus(PaymentStatus.PENDING);
            payment.setAmount(order.getTotalAmount());
            payment.setStripePaymentIntentId(intent.getId());
            payment.setClientSecret(intent.getClientSecret());
            payment.setPaymentMode(PaymentMode.STRIPE);

            paymentRepository.save(payment);

            PaymentIntentResponseDto response = new PaymentIntentResponseDto(
                    orderId,
                    intent.getId(),
                    intent.getClientSecret(),
                    PaymentStatus.PENDING,
                    order.getTotalAmount());

            storeIntentIdempotency(key, orderId, response);

            return response;

        } catch (Exception e) {
            log.error("Stripe error", e);
            throw new RuntimeException("Stripe error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentIntentResponseDto retryPayment(Long orderId, String key) {

        log.info("Retrying payment. OrderId: {}", orderId);

        PaymentIntentResponseDto stored = getStoredIntentResponse(key);
        if (stored != null)
            return stored;

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.isPaid()) {
            throw new RuntimeException("Order already paid");
        }

        PaymentEntity payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        try {
            Long amountInPaise = order.getTotalAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(key)
                    .build();

            PaymentIntent intent = PaymentIntent.create(
                    PaymentIntentCreateParams.builder()
                            .setAmount(amountInPaise)
                            .setCurrency("inr")
                            .putMetadata("orderId", orderId.toString())
                            .build(),
                    options);

            payment.setStripePaymentIntentId(intent.getId());
            payment.setClientSecret(intent.getClientSecret());
            payment.setStatus(PaymentStatus.PENDING);

            paymentRepository.save(payment);

            PaymentIntentResponseDto response = new PaymentIntentResponseDto(
                    orderId,
                    intent.getId(),
                    intent.getClientSecret(),
                    PaymentStatus.PENDING,
                    order.getTotalAmount());

            storeIntentIdempotency(key, orderId, response);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Retry failed");
        }
    }

    @Transactional
    public void handlePaymentFailure(String paymentIntentId, String eventId) {

        log.warn("Handling payment failure. PaymentIntentId: {}", paymentIntentId);

        if (processedWebhookRepository.existsById(eventId)) {
            log.warn("Duplicate webhook ignored. EventId: {}", eventId);
            return;
        }

        PaymentEntity payment = paymentRepository
                .findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.FAILED) {
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);

        OrderEntity order = payment.getOrder();

        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            log.warn("Skipping failure, payment already SUCCESS. OrderId: {}", order.getId());
            return;
        }

        order.markPaymentFailed();

        orderService.cancelOrder(order);

        log.warn("Payment failure handled. OrderId: {}", order.getId());

        ProcessedWebhookEntity entity = new ProcessedWebhookEntity();
        entity.setEventId(eventId);
        entity.setProcessedAt(LocalDateTime.now());

        processedWebhookRepository.save(entity);
    }

    private PaymentIntentResponseDto getStoredIntentResponse(String key) {
        Optional<IdempotencyKeyEntity> existing = idempotencyKeyRepository.findByIdempotencyKey(key);

        if (existing.isPresent()) {
            try {
                return objectMapper.readValue(
                        existing.get().getResponseBody(),
                        PaymentIntentResponseDto.class);
            } catch (Exception e) {
                throw new RuntimeException("Parse error");
            }
        }
        return null;
    }

    private void storeIntentIdempotency(String key, Long orderId, PaymentIntentResponseDto response) {
        try {
            IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
            entity.setIdempotencyKey(key);
            entity.setRequestPath("/payments/" + orderId + "/intent");
            entity.setResponseBody(objectMapper.writeValueAsString(response));
            idempotencyKeyRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Idempotency store failed");
        }
    }

    @Transactional
    public void handlePaymentSuccess(String paymentIntentId, Long orderId, String eventId) {

        log.info("Handling payment success. PaymentIntentId: {}, OrderId: {}",
                paymentIntentId, orderId);

        if (processedWebhookRepository.existsById(eventId)) {
            log.warn("Duplicate webhook ignored. EventId: {}", eventId);
            return;
        }

        PaymentEntity payment = paymentRepository
                .findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> {
                    log.error("Payment not found for PaymentIntentId: {}", paymentIntentId);
                    return new RuntimeException("Payment not found");
                });

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.warn("Duplicate webhook ignored. Payment already SUCCESS. PaymentIntentId: {}",
                    paymentIntentId);
            return;
        }

        payment.setStatus(PaymentStatus.SUCCESS);

        log.info("Payment marked SUCCESS. PaymentIntentId: {}", paymentIntentId);

        OrderEntity order = payment.getOrder();

        if (!order.isPaid()) {

            order.markPaymentSuccess(paymentIntentId);

            order.updateStatus(OrderStatus.CONFIRMED);

            log.info("Order marked as CONFIRMED. OrderId: {}", order.getId());

        } else {
            log.warn("Order already processed. OrderId: {}", order.getId());
        }

        ProcessedWebhookEntity entity = new ProcessedWebhookEntity();
        entity.setEventId(eventId);
        entity.setProcessedAt(LocalDateTime.now());

        processedWebhookRepository.save(entity);
    }
}