package dev.studentpp1.streamingservice.subscription.domain.exception;

public class ActiveSubscriptionAlreadyExistsException extends SubscriptionDomainException {
    public ActiveSubscriptionAlreadyExistsException(Long id, String planName) {
        super("User " + id + " already has active subscription for plan: " + planName);
    }

    public ActiveSubscriptionAlreadyExistsException(String message) {
        super(message);
    }
}
