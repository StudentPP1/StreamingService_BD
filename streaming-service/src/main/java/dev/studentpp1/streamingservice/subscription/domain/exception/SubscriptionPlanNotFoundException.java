package dev.studentpp1.streamingservice.subscription.domain.exception;

public class SubscriptionPlanNotFoundException extends SubscriptionDomainException {
    public SubscriptionPlanNotFoundException(Long id) {
        super("Subscription plan not found with id: " + id);
    }
    public SubscriptionPlanNotFoundException(String name) {
        super("Subscription plan not found with name: " + name);
    }
}
