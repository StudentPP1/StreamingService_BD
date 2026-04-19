package dev.studentpp1.streamingservice.payments.api.checkout;

public record PaymentCheckoutResponse(
        String status,
        String message,
        String sessionId,
        String sessionUrl
) {
}

