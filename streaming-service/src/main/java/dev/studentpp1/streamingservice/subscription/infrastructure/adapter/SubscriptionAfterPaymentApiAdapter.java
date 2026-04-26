package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.common.event.EventBus;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionActivatedEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionFailedEvent;
import dev.studentpp1.streamingservice.subscription.api.payment.SubscriptionAfterPaymentApi;
import dev.studentpp1.streamingservice.subscription.application.command.CancelSubscriptionAfterPaymentFailureHandler;
import dev.studentpp1.streamingservice.subscription.application.command.CreateUserSubscriptionAfterPaymentHandler;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class SubscriptionAfterPaymentApiAdapter implements SubscriptionAfterPaymentApi {

    private final CreateUserSubscriptionAfterPaymentHandler createHandler;
    private final CancelSubscriptionAfterPaymentFailureHandler cancelHandler;
    private final EventBus eventBus;
    // private final SubscriptionNotification notification;

    @Override
    public Long onPaymentSucceeded(Long paymentId, Long userId, String userEmail, String planName) {
        UserSubscription subscription = createHandler.handle(planName, userId);
        SubscriptionActivatedEvent event = new SubscriptionActivatedEvent(
                subscription.getId(), userId, userEmail, planName, subscription.getEndTime(), Instant.now()
        );
        eventBus.publish(event);
        return subscription.getId();
    }

    @Override
    public void onPaymentFailed(Long userId, String userEmail, String planName,
                                Long existingSubscriptionId, String reason) {
        if (existingSubscriptionId != null) {
            cancelHandler.handle(existingSubscriptionId);
        }
//        try {
//            notification.notifyFailed(userEmail, planName, reason);
//        } catch (Exception e) {
//            System.err.println("Failed to send subscription failure notification: " + e.getMessage());
//        }
        eventBus.publish(new SubscriptionFailedEvent(
                userId, userEmail, planName, reason, Instant.now()
        ));
    }
}

