package dev.studentpp1.streamingservice.subscription.application.command;

import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionService;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionCommandHandler {

    private final SubscriptionService subscriptionService;

    public CheckoutResult handle(SubscribeUserCommand command) {
        return subscriptionService.subscribeUser(command.request(), command.userId());
    }

    public CheckoutResult handle(CreateFamilySubscriptionCommand command) {
        return subscriptionService.createFamilySubscription(command.request(), command.userId());
    }

    public void handle(CancelSubscriptionCommand command) {
        subscriptionService.cancelSubscription(command.subscriptionId(), command.userId());
    }
}

