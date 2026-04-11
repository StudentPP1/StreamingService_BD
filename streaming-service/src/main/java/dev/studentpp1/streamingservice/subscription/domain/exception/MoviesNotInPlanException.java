package dev.studentpp1.streamingservice.subscription.domain.exception;

public class MoviesNotInPlanException extends SubscriptionDomainException {
    public MoviesNotInPlanException() {
        super("None of the specified movies are in the plan");
    }
}
