package dev.studentpp1.streamingservice.subscription.application.event.handler;

import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateUserSubscriptionAfterPaymentHandler {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    @Transactional
    public UserSubscription handle(String planName, Long userId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findByName(planName)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(planName));
        UserSubscription subscription = UserSubscription.create(
                userId,
                plan.getId(),
                LocalDateTime.now(),
                plan.getDuration()
        );
        UserSubscription saved = userSubscriptionRepository.save(subscription);
        log.info("Subscription created: userId={}, plan={}", userId, planName);
        return saved;
    }
}
