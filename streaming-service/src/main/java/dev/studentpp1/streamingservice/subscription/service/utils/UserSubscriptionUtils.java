package dev.studentpp1.streamingservice.subscription.service.utils;

import dev.studentpp1.streamingservice.subscription.entity.UserSubscription;
import dev.studentpp1.streamingservice.subscription.exception.SubscriptionNotFoundException;
import dev.studentpp1.streamingservice.subscription.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSubscriptionUtils {

    private final UserSubscriptionRepository userSubscriptionRepository;

    public UserSubscription findById(Long subscriptionId) {
        return userSubscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));
    }

    public UserSubscription findByIdWithLock(Long subscriptionId) {
        return userSubscriptionRepository.findByIdWithLock(subscriptionId)
            .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));
    }
}
