package dev.studentpp1.streamingservice.payments.presentation.dto;

public record PaymentResponse(
        String status,
        String message,
        String sessionId,
        String sessionUrl
) {
}
