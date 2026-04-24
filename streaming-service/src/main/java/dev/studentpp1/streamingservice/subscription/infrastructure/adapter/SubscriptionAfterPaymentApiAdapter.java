package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.common.event.EventBus;
// import dev.studentpp1.streamingservice.notification.port.SubscriptionNotification;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionActivatedEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionFailedEvent;
import dev.studentpp1.streamingservice.subscription.api.payment.SubscriptionAfterPaymentApi;
import dev.studentpp1.streamingservice.subscription.application.command.CancelSubscriptionAfterPaymentFailureHandler;
import dev.studentpp1.streamingservice.subscription.application.command.CreateUserSubscriptionAfterPaymentHandler;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class SubscriptionAfterPaymentApiAdapter implements SubscriptionAfterPaymentApi {

    private final CreateUserSubscriptionAfterPaymentHandler createHandler;
    private final CancelSubscriptionAfterPaymentFailureHandler cancelHandler;
    private final EventBus eventBus;
    // private final SubscriptionNotification notification;

    @Override
    @Transactional
    public Long onPaymentSucceeded(Long paymentId, Long userId, String userEmail, String planName) {
        UserSubscription subscription = createHandler.handle(planName, userId);
        SubscriptionActivatedEvent event = new SubscriptionActivatedEvent(
                subscription.getId(), userId, userEmail, planName, subscription.getEndTime(), Instant.now()
        );
        eventBus.publish(event);
        return subscription.getId();
    }

    @Override
    @Transactional
    public void onPaymentFailed(Long userId, String userEmail, String planName,
                                Long existingSubscriptionId, String reason) {
        if (existingSubscriptionId != null) {
            cancelHandler.handle(existingSubscriptionId);
        }
        // notification.notifyFailed(userEmail, planName, reason);
        eventBus.publish(new SubscriptionFailedEvent(
                userId, userEmail, planName, reason, Instant.now()
        ));
    }
}

