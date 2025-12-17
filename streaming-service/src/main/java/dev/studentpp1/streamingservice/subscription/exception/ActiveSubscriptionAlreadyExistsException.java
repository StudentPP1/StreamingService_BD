package dev.studentpp1.streamingservice.subscription.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ActiveSubscriptionAlreadyExistsException extends RuntimeException {

    public ActiveSubscriptionAlreadyExistsException(String userEmail, String planName) {
        super("User '%s' already has an active '%s' subscription. Cancellation required."
            .formatted(userEmail, planName));
    }
}
