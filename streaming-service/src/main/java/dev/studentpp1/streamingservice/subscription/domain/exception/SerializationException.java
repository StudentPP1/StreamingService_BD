package dev.studentpp1.streamingservice.subscription.domain.exception;

public class SerializationException extends SubscriptionDomainException {
    public SerializationException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}