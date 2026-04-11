package dev.studentpp1.streamingservice.subscription.domain.exception;

public class SubscriptionNotFoundException extends SubscriptionDomainException {
    public SubscriptionNotFoundException(Long id) {
        super("Subscription not found with id: " + id);
    }
}