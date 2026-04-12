package dev.studentpp1.streamingservice.subscription.application.command.subscription;

import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionAccessDeniedException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CancelSubscriptionHandler {

    private final UserSubscriptionRepository userSubscriptionRepository;

    @Transactional
    public void handle(CancelSubscriptionCommand command) {
        UserSubscription subscription = userSubscriptionRepository
                .findByIdWithLock(command.subscriptionId())
                .orElseThrow(() -> new SubscriptionNotFoundException(command.subscriptionId()));

        if (!subscription.getUserId().equals(command.userId())) {
            throw new SubscriptionAccessDeniedException();
        }

        subscription.cancel();
        userSubscriptionRepository.save(subscription);
    }
}

