package dev.studentpp1.streamingservice.subscription.application.command.subscription;

import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.factory.UserSubscriptionFactory;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateUserSubscriptionAfterPaymentHandler {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserSubscriptionFactory userSubscriptionFactory;

    @Transactional
    public UserSubscription handle(String planName, Long userId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findByName(planName)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(planName));
        UserSubscription subscription = userSubscriptionFactory.create(
                userId, plan.getId(), LocalDateTime.now(), plan.getDuration());
        return userSubscriptionRepository.save(subscription);
    }
}

