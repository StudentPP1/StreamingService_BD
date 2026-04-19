package dev.studentpp1.streamingservice.analytics.internal.acl;

import dev.studentpp1.streamingservice.analytics.internal.model.PaymentMetric;
import dev.studentpp1.streamingservice.analytics.internal.model.SubscriptionMetric;
import dev.studentpp1.streamingservice.payments.api.event.PaymentFailedEvent;
import dev.studentpp1.streamingservice.payments.api.event.PaymentSucceededEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionActivatedEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionFailedEvent;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsAclTranslator {

    public PaymentMetric toPaymentMetric(PaymentSucceededEvent event) {
        return new PaymentMetric(true);
    }

    public PaymentMetric toPaymentMetric(PaymentFailedEvent event) {
        return new PaymentMetric(false);
    }

    public SubscriptionMetric toSubscriptionMetric(SubscriptionActivatedEvent event) {
        return new SubscriptionMetric(true);
    }

    public SubscriptionMetric toSubscriptionMetric(SubscriptionFailedEvent event) {
        return new SubscriptionMetric(false);
    }
}

