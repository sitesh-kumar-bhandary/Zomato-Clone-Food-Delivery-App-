package com.siteshkumar.zomato_clone_backend.service;

import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.siteshkumar.zomato_clone_backend.service.Impl.OrderServiceImpl;
import com.siteshkumar.zomato_clone_backend.service.Impl.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookService {

    private final OrderServiceImpl orderService;
    private final PaymentServiceImpl paymentService;

    public void handleEvent(Event event) {

        String eventType = event.getType();
        log.info("Handling Stripe event: {}", eventType);

        switch (eventType) {

            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(event);
                break;

            case "payment_intent.payment_failed":
                handlePaymentIntentFailed(event);
                break;

            default:
                log.warn("Unhandled Stripe event type: {}", eventType);
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {

        log.info("Processing payment_intent.succeeded event");

        var deserializer = event.getDataObjectDeserializer();

        if (!deserializer.getObject().isPresent()) {
            log.error("Failed to deserialize Stripe event object");
            throw new RuntimeException("Stripe event deserialization failed");
        }

        PaymentIntent intent = (PaymentIntent) deserializer.getObject().get();

        String orderIdStr = intent.getMetadata().get("orderId");

        if (orderIdStr == null) {
            log.error("OrderId missing in metadata");
            throw new RuntimeException("OrderId missing in metadata");
        }

        Long orderId = Long.valueOf(orderIdStr);

        log.info("PaymentIntent succeeded. OrderId: {}, IntentId: {}",
                orderId, intent.getId());

        try {
            paymentService.handlePaymentSuccess(intent.getId(), orderId, event.getId());

            orderService.markOrderAsPaid(orderId);

        } catch (Exception e) {
            log.error("Error processing payment success webhook", e);
            throw e;
        }
    }

    private void handlePaymentIntentFailed(Event event) {

        log.warn("Processing payment_intent.payment_failed event");

        var deserializer = event.getDataObjectDeserializer();

        if (!deserializer.getObject().isPresent()) {
            log.error("Failed to deserialize Stripe event object");
            throw new RuntimeException("Deserialization failed");
        }

        PaymentIntent intent = (PaymentIntent) deserializer.getObject().get();

        log.warn("Payment failed for PaymentIntentId: {}", intent.getId());

        paymentService.handlePaymentFailure(intent.getId());
    }
}