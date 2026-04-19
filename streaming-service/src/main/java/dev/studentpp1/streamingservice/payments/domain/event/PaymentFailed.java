package dev.studentpp1.streamingservice.payments.domain.event;

public record PaymentFailed(
        Long paymentId,
        Long userId,
        String userEmail,
        String planName,
        String providerSessionId,
        Long userSubscriptionId,
        String reason
) {
}

