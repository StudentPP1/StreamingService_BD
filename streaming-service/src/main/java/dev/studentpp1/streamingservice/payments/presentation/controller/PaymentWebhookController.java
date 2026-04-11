package dev.studentpp1.streamingservice.payments.presentation.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import dev.studentpp1.streamingservice.payments.application.cqs.PaymentWebhookCommandHandler;
import dev.studentpp1.streamingservice.payments.application.cqs.PaymentsCqs.HandleStripeEventCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments/webhook")
@RequiredArgsConstructor
public class PaymentWebhookController {

    @Value("${app.payment.webhook.key}")
    private String endpointSecret;

    private final PaymentWebhookCommandHandler paymentWebhookCommandHandler;

    @PostMapping
    public ResponseEntity<Void> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) throws SignatureVerificationException {
        Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        paymentWebhookCommandHandler.handle(new HandleStripeEventCommand(event));
        return ResponseEntity.ok().build();
    }
}