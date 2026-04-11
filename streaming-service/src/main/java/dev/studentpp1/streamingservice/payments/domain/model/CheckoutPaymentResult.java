package dev.studentpp1.streamingservice.payments.domain.model;

public record CheckoutPaymentResult(
        String status,
        String message,
        String sessionId,
        String sessionUrl
) {
}

