package dev.studentpp1.streamingservice.subscription.application.event.listener;

import dev.studentpp1.streamingservice.payments.api.event.PaymentFailedEvent;
import dev.studentpp1.streamingservice.payments.api.event.PaymentSucceededEvent;
import dev.studentpp1.streamingservice.subscription.application.event.handler.CancelSubscriptionAfterPaymentFailureHandler;
import dev.studentpp1.streamingservice.subscription.application.event.handler.CreateUserSubscriptionAfterPaymentHandler;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionActivatedEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionFailedEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionLinkedToPaymentEvent;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class SubscriptionPaymentListener {

    private final CreateUserSubscriptionAfterPaymentHandler createUserSubscriptionAfterPaymentHandler;
    private final CancelSubscriptionAfterPaymentFailureHandler cancelSubscriptionAfterPaymentFailureHandler;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    @Transactional
    public void onPaymentSucceeded(PaymentSucceededEvent event) {
        UserSubscription subscription = createUserSubscriptionAfterPaymentHandler
                .handle(event.planName(), event.userId());
        eventPublisher.publishEvent(new SubscriptionLinkedToPaymentEvent(
                event.paymentId(),
                event.providerSessionId(),
                subscription.getId(),
                Instant.now()
        ));
        eventPublisher.publishEvent(new SubscriptionActivatedEvent(
                subscription.getId(),
                event.userId(),
                event.userEmail(),
                event.planName(),
                subscription.getEndTime(),
                Instant.now()
        ));
    }

    @EventListener
    @Transactional
    public void onPaymentFailed(PaymentFailedEvent event) {
        if (event.userSubscriptionId() != null) {
            cancelSubscriptionAfterPaymentFailureHandler.handle(event.userSubscriptionId());
        }
        eventPublisher.publishEvent(new SubscriptionFailedEvent(
                event.userId(),
                event.userEmail(),
                event.planName(),
                event.reason(),
                Instant.now()
        ));
    }
}
