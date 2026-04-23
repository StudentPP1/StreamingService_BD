package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.common.event.EventBus;
import dev.studentpp1.streamingservice.notification.port.SubscriptionNotification;
import dev.studentpp1.streamingservice.payments.domain.port.SubscriptionAfterPaymentPort;
import dev.studentpp1.streamingservice.subscription.application.command.CancelSubscriptionAfterPaymentFailureHandler;
import dev.studentpp1.streamingservice.subscription.application.command.CreateUserSubscriptionAfterPaymentHandler;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionActivated;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class SubscriptionAfterPaymentPortAdapter implements SubscriptionAfterPaymentPort {

    private final CreateUserSubscriptionAfterPaymentHandler createHandler;
    private final CancelSubscriptionAfterPaymentFailureHandler cancelHandler;
    private final EventBus eventBus;
    private final SubscriptionNotification subscriptionNotification;

    @Override
    @Transactional
    public Long onPaymentSucceeded(Long paymentId, Long userId, String userEmail, String planName) {
        UserSubscription subscription = createHandler.handle(planName, userId);
        // Async communication: publish event to EventBus, notification runs in a new thread
        eventBus.publish(new SubscriptionActivated(
                subscription.getId(), userId, userEmail, planName, subscription.getEndTime(), Instant.now()
        ));
        return subscription.getId();
    }

    @Override
    @Transactional
    public void onPaymentFailed(Long userId, String userEmail, String planName, Long existingSubscriptionId, String reason) {
        if (existingSubscriptionId != null) {
            cancelHandler.handle(existingSubscriptionId);
        }
        // Sync communication: direct call to notification port, caller waits for completion
        subscriptionNotification.notifyFailed(userEmail, planName, reason);
    }
}
