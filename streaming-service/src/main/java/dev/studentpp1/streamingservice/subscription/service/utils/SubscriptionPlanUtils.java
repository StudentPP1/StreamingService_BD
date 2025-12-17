package dev.studentpp1.streamingservice.subscription.service.utils;

import dev.studentpp1.streamingservice.subscription.entity.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanUtils {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlan findByName(String planName) {
        return subscriptionPlanRepository.findByName(planName)
            .orElseThrow(() -> new SubscriptionPlanNotFoundException(planName));
    }

    public SubscriptionPlan findById(Long subscriptionId) {
        return subscriptionPlanRepository.findById(subscriptionId)
            .orElseThrow(() -> new SubscriptionPlanNotFoundException(subscriptionId));
    }

    public SubscriptionPlan findByIdWithMovies(Long subscriptionId) {
        return subscriptionPlanRepository.findWithMoviesById(subscriptionId)
            .orElseThrow(() -> new SubscriptionPlanNotFoundException(subscriptionId));
    }

    public SubscriptionPlan findByIdWithLock(Long subscriptionId) {
        return subscriptionPlanRepository.findByIdWithLock(subscriptionId)
            .orElseThrow(() -> new SubscriptionPlanNotFoundException(subscriptionId));
    }
}
