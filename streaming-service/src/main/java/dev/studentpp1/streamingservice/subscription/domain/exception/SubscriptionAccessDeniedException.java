package dev.studentpp1.streamingservice.subscription.domain.exception;

public class SubscriptionAccessDeniedException extends SubscriptionDomainException {
    public SubscriptionAccessDeniedException() {
        super("Access denied to this subscription");
    }
}
