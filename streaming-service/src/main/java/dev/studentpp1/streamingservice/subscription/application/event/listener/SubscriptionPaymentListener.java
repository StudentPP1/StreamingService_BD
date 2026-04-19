package dev.studentpp1.streamingservice.subscription.application.event.listener;

import dev.studentpp1.streamingservice.payments.domain.event.PaymentFailed;
import dev.studentpp1.streamingservice.payments.domain.event.PaymentSucceeded;
import dev.studentpp1.streamingservice.subscription.application.event.handler.CancelSubscriptionAfterPaymentFailureHandler;
import dev.studentpp1.streamingservice.subscription.application.event.handler.CreateUserSubscriptionAfterPaymentHandler;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionActivated;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionFailed;
import dev.studentpp1.streamingservice.subscription.domain.event.SubscriptionLinkedToPayment;
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
    public void onPaymentSucceeded(PaymentSucceeded event) {
        UserSubscription subscription = createUserSubscriptionAfterPaymentHandler
                .handle(event.planName(), event.userId());
        eventPublisher.publishEvent(new SubscriptionLinkedToPayment(
                event.paymentId(),
                event.providerSessionId(),
                subscription.getId(),
                Instant.now()
        ));
        eventPublisher.publishEvent(new SubscriptionActivated(
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
    public void onPaymentFailed(PaymentFailed event) {
        if (event.userSubscriptionId() != null) {
            cancelSubscriptionAfterPaymentFailureHandler.handle(event.userSubscriptionId());
        }
        eventPublisher.publishEvent(new SubscriptionFailed(
                event.userId(),
                event.userEmail(),
                event.planName(),
                event.reason(),
                Instant.now()
        ));
    }
}

