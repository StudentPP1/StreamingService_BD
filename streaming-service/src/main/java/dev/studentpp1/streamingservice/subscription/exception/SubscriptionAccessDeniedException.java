package dev.studentpp1.streamingservice.subscription.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class SubscriptionAccessDeniedException extends RuntimeException {
    public SubscriptionAccessDeniedException() {
        super("You are not authorized to cancel this subscription");
    }
}

