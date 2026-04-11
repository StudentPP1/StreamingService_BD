package dev.studentpp1.streamingservice.subscription.domain.exception;

public class SubscriptionNotActiveException extends SubscriptionDomainException {
    public SubscriptionNotActiveException() {
        super("Subscription is not active");
    }
}
