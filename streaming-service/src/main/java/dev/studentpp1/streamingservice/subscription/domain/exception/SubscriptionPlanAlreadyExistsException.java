package dev.studentpp1.streamingservice.subscription.domain.exception;

public class SubscriptionPlanAlreadyExistsException extends SubscriptionDomainException {
    public SubscriptionPlanAlreadyExistsException(String name) {
        super("Subscription plan already exists: " + name);
    }
}
