package dev.studentpp1.streamingservice.payments.domain.model;

public record CheckoutPaymentResponse(
        String status,
        String message,
        String sessionId,
        String sessionUrl
) {
}

