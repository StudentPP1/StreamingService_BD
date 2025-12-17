package dev.studentpp1.streamingservice.subscription.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SubscriptionPlanAlreadyExistsException extends RuntimeException {

    public SubscriptionPlanAlreadyExistsException(String name) {
        super("Subscription plan with name '%s' already exists".formatted(name));
    }
}

