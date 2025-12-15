package dev.studentpp1.streamingservice.subscription.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SubscriptionNotActiveException extends RuntimeException {

    public SubscriptionNotActiveException() {
        super("Only active subscriptions can be cancelled");
    }
}
