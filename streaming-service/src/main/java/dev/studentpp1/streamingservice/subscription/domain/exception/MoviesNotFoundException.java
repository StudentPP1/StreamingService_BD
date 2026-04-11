package dev.studentpp1.streamingservice.subscription.domain.exception;

import java.util.List;

public class MoviesNotFoundException extends SubscriptionDomainException {
    public MoviesNotFoundException(List<Long> ids) {
        super("Movies not found with ids: " + ids);
    }
}
