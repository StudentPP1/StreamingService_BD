package dev.studentpp1.streamingservice.subscription.domain.port;

import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;

import java.util.List;

public interface SubscriptionActivationPort {
    UserSubscription createUserSubscription(String planName, Long userId);
    List<UserSubscription> createFamilySubscriptionAfterPayment(Long userId, String planName, List<String> memberEmails);
}
