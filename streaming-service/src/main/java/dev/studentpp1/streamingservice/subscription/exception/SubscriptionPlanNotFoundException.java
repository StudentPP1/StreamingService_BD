package dev.studentpp1.streamingservice.subscription.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SubscriptionPlanNotFoundException extends RuntimeException {
    public SubscriptionPlanNotFoundException(Long id) {
        super("Subscription plan not found with id " + id);
    }

    public SubscriptionPlanNotFoundException(String name) {
        super("Subscription plan not found with name " + name);
    }
}
