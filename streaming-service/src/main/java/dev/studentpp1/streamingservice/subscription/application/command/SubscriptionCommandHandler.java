package dev.studentpp1.streamingservice.subscription.application.command;

import dev.studentpp1.streamingservice.subscription.application.command.subscription.*;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionActivationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionCommandHandler implements SubscriptionActivationPort {

    private final SubscribeUserHandler subscribeUserHandler;
    private final CreateFamilySubscriptionHandler createFamilySubscriptionHandler;
    private final CancelSubscriptionHandler cancelSubscriptionHandler;
    private final CreateUserSubscriptionAfterPaymentHandler createUserSubscriptionAfterPaymentHandler;
    private final CreateFamilySubscriptionAfterPaymentHandler createFamilySubscriptionAfterPaymentHandler;

    @Override
    public UserSubscription createUserSubscription(String planName, Long userId) {
        return createUserSubscriptionAfterPaymentHandler.handle(planName, userId);
    }

    @Override
    public List<UserSubscription> createFamilySubscriptionAfterPayment(
            Long userId, String planName, List<String> memberEmails) {
        return createFamilySubscriptionAfterPaymentHandler.handle(userId, planName, memberEmails);
    }

    public void handle(SubscribeUserCommand command) {
        subscribeUserHandler.handle(command);
    }

    public void handle(CreateFamilySubscriptionCommand command) {
        createFamilySubscriptionHandler.handle(command);
    }

    public void handle(CancelSubscriptionCommand command) {
        cancelSubscriptionHandler.handle(command);
    }
}

