package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.common.event.EventBus;
import dev.studentpp1.streamingservice.notification.port.SubscriptionNotification;
import dev.studentpp1.streamingservice.subscription.api.payment.SubscriptionAfterPaymentApi;
import dev.studentpp1.streamingservice.subscription.application.command.CancelSubscriptionAfterPaymentFailureHandler;
import dev.studentpp1.streamingservice.subscription.application.command.CreateUserSubscriptionAfterPaymentHandler;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionActivated;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionAfterPaymentPortAdapter implements SubscriptionAfterPaymentApi {

    private final CreateUserSubscriptionAfterPaymentHandler createHandler;
    private final CancelSubscriptionAfterPaymentFailureHandler cancelHandler;
    private final EventBus eventBus;
    private final SubscriptionNotification subscriptionNotification;

    @Override
    public Long onPaymentSucceeded(Long paymentId, Long userId, String userEmail, String planName) {
        UserSubscription subscription = createHandler.handle(planName, userId);
        eventBus.publish(new SubscriptionActivated(
                subscription.getId(), userId, userEmail, planName, subscription.getEndTime(), Instant.now()
        ));
        return subscription.getId();
    }

    @Override
    public void onPaymentFailed(Long userId, String userEmail, String planName, Long existingSubscriptionId, String reason) {
        if (existingSubscriptionId != null) {
            cancelHandler.handle(existingSubscriptionId);
        }
        try {
            subscriptionNotification.notifyFailed(userEmail, planName, reason);
        } catch (Exception e) {
            log.error("Failed to send payment failure email: {}", e.getMessage());
        }
    }
}
