package dev.studentpp1.streamingservice.subscription.api.payment;

public interface SubscriptionAfterPaymentApi {
    Long onPaymentSucceeded(Long paymentId, Long userId, String userEmail, String planName);
    void onPaymentFailed(Long userId, String userEmail, String planName, Long existingSubscriptionId, String reason);
}

