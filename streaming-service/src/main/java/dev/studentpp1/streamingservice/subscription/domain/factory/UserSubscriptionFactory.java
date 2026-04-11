package dev.studentpp1.streamingservice.subscription.domain.factory;

import dev.studentpp1.streamingservice.subscription.domain.exception.ActiveSubscriptionAlreadyExistsException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionDomainException;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;

import java.time.LocalDateTime;

public class UserSubscriptionFactory {

    private final UserSubscriptionRepository userSubscriptionRepository;

    public UserSubscriptionFactory(UserSubscriptionRepository userSubscriptionRepository) {
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    public UserSubscription create(Long userId, Long planId,
                                   LocalDateTime startTime, Integer durationDays) {
        boolean hasActive = userSubscriptionRepository.findByUserId(userId)
                .stream()
                .anyMatch(s -> s.getPlanId().equals(planId)
                        && s.getStatus() == SubscriptionStatus.ACTIVE);

        if (hasActive) {
            throw new ActiveSubscriptionAlreadyExistsException(
                    userId, planId.toString());
        }

        return UserSubscription.create(userId, planId, startTime, durationDays);
    }
}