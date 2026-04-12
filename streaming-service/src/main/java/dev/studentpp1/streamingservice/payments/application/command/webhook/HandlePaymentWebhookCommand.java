package dev.studentpp1.streamingservice.payments.application.command.webhook;

import com.stripe.model.Event;

public record HandlePaymentWebhookCommand(Event event) {
}

