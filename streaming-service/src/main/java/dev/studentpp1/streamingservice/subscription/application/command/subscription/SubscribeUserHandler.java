package dev.studentpp1.streamingservice.subscription.application.command.subscription;

import dev.studentpp1.streamingservice.subscription.domain.exception.ActiveSubscriptionAlreadyExistsException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutCommand;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionPaymentGateway;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscribeUserHandler {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriptionPaymentGateway paymentGateway;

    @Transactional
    public CheckoutResult handle(SubscribeUserCommand command) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(command.planId())
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(command.planId()));
        boolean hasActive = userSubscriptionRepository.existsByUserIdInAndPlanIdAndStatus(
                List.of(command.userId()), plan.getId(), SubscriptionStatus.ACTIVE);
        if (hasActive) {
            throw new ActiveSubscriptionAlreadyExistsException("User already has an active plan: " + plan.getName());
        }
        return paymentGateway.generateCheckout(new CheckoutCommand(
                plan.getName(), plan.getPrice(), command.userId(), command.userEmail()));
    }
}
