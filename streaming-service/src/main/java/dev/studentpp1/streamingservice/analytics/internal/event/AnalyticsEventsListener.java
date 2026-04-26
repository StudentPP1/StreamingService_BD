package dev.studentpp1.streamingservice.analytics.internal.event;

import dev.studentpp1.streamingservice.analytics.internal.acl.AnalyticsAclTranslator;
import dev.studentpp1.streamingservice.analytics.internal.impl.AnalyticsData;
import dev.studentpp1.streamingservice.common.event.EventBus;
import dev.studentpp1.streamingservice.payments.api.event.PaymentFailedEvent;
import dev.studentpp1.streamingservice.payments.api.event.PaymentSucceededEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionActivatedEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionFailedEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsEventsListener {

    private final EventBus eventBus;
    private final AnalyticsAclTranslator aclTranslator;
    private final AnalyticsData data;

    @PostConstruct
    void registerSubscriptions() {
        eventBus.subscribeAsync(PaymentSucceededEvent.class, this::onPaymentSucceeded);
        eventBus.subscribeAsync(PaymentFailedEvent.class, this::onPaymentFailed);
        eventBus.subscribeAsync(SubscriptionActivatedEvent.class, this::onSubscriptionActivated);
        eventBus.subscribeAsync(SubscriptionFailedEvent.class, this::onSubscriptionFailed);
    }

    public void onPaymentSucceeded(PaymentSucceededEvent event) {
        data.apply(aclTranslator.toPaymentMetric(event));
    }

    public void onPaymentFailed(PaymentFailedEvent event) {
        data.apply(aclTranslator.toPaymentMetric(event));
    }

    public void onSubscriptionActivated(SubscriptionActivatedEvent event) {
        data.apply(aclTranslator.toSubscriptionMetric(event));
    }

    public void onSubscriptionFailed(SubscriptionFailedEvent event) {
        data.apply(aclTranslator.toSubscriptionMetric(event));
    }
}

