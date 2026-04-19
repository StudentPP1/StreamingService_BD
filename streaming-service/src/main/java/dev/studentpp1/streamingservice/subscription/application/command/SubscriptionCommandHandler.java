package dev.studentpp1.streamingservice.subscription.application.command;

import dev.studentpp1.streamingservice.subscription.application.command.subscription.*;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionCommandHandler {

    private final SubscribeUserHandler subscribeUserHandler;
    private final CancelSubscriptionHandler cancelSubscriptionHandler;

    public CheckoutResult handle(SubscribeUserCommand command) {
        return subscribeUserHandler.handle(command);
    }

    public void handle(CancelSubscriptionCommand command) {
        cancelSubscriptionHandler.handle(command);
    }
}
