package dev.studentpp1.streamingservice.subscription.domain.exception;

public class InvalidFamilyMemberException extends SubscriptionDomainException {
    public InvalidFamilyMemberException() {
        super("Main user cannot be a family member");
    }
}