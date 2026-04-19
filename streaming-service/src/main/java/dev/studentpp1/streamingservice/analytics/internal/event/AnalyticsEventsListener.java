package dev.studentpp1.streamingservice.analytics.internal.event;

import dev.studentpp1.streamingservice.analytics.internal.acl.AnalyticsAclTranslator;
import dev.studentpp1.streamingservice.analytics.internal.projection.AnalyticsProjectionStore;
import dev.studentpp1.streamingservice.payments.api.event.PaymentFailedEvent;
import dev.studentpp1.streamingservice.payments.api.event.PaymentSucceededEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionActivatedEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsEventsListener {

    private final AnalyticsAclTranslator aclTranslator;
    private final AnalyticsProjectionStore projectionStore;

    @Async
    @EventListener
    public void onPaymentSucceeded(PaymentSucceededEvent event) {
        projectionStore.apply(aclTranslator.toPaymentMetric(event));
    }

    @Async
    @EventListener
    public void onPaymentFailed(PaymentFailedEvent event) {
        projectionStore.apply(aclTranslator.toPaymentMetric(event));
    }

    @Async
    @EventListener
    public void onSubscriptionActivated(SubscriptionActivatedEvent event) {
        projectionStore.apply(aclTranslator.toSubscriptionMetric(event));
    }

    @Async
    @EventListener
    public void onSubscriptionFailed(SubscriptionFailedEvent event) {
        projectionStore.apply(aclTranslator.toSubscriptionMetric(event));
    }
}

