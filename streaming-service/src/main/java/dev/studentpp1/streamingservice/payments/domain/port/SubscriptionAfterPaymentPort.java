package dev.studentpp1.streamingservice.payments.domain.port;

public interface SubscriptionAfterPaymentPort {
    Long onPaymentSucceeded(Long paymentId, Long userId, String userEmail, String planName);
    void onPaymentFailed(Long userId, String userEmail, String planName, Long existingSubscriptionId, String reason);
}
