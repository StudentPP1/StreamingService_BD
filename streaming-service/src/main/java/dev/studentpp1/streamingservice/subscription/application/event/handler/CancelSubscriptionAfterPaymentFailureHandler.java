package dev.studentpp1.streamingservice.subscription.application.event.handler;

import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelSubscriptionAfterPaymentFailureHandler {

    private final UserSubscriptionRepository userSubscriptionRepository;

    @Transactional
    public void handle(Long subscriptionId) {
        UserSubscription subscription = userSubscriptionRepository
                .findByIdWithLock(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            return;
        }
        subscription.cancel();
        userSubscriptionRepository.save(subscription);
        log.info("Subscription cancelled due to payment failure: subscriptionId={}", subscriptionId);
    }
}

