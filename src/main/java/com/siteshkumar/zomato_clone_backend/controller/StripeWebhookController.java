package com.siteshkumar.zomato_clone_backend.controller;

import java.io.BufferedReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.siteshkumar.zomato_clone_backend.service.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final StripeWebhookService stripeWebhookService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;
    
    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
                            HttpServletRequest request, 
                            @RequestHeader("Stripe-Signature") String sigHeader){

        String payload = getRequestBody(request);
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        }

        catch (SignatureVerificationException e){
            return ResponseEntity.status(400).body("Invalid Signature");
        }

        catch(Exception e) {
            return ResponseEntity.status(400).body("Webhook error: "+e.getMessage());
        }

        stripeWebhookService.handleEvent(event);
        return ResponseEntity.ok("Success");

    }

    private String getRequestBody(HttpServletRequest request) {
        StringBuilder payload = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                payload.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read request body");
        }

        return payload.toString();
    }
}
