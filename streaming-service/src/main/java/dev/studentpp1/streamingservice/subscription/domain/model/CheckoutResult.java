package dev.studentpp1.streamingservice.subscription.domain.model;

public record CheckoutResult(
        String status,
        String message,
        String sessionId,
        String url
) {}
