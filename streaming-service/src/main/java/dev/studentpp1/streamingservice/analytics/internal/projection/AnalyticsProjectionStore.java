package dev.studentpp1.streamingservice.analytics.internal.projection;

import dev.studentpp1.streamingservice.analytics.api.AnalyticsSummaryView;
import dev.studentpp1.streamingservice.analytics.internal.model.PaymentMetric;
import dev.studentpp1.streamingservice.analytics.internal.model.SubscriptionMetric;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class AnalyticsProjectionStore {
    private final AtomicLong successfulPayments = new AtomicLong(0);
    private final AtomicLong failedPayments = new AtomicLong(0);
    private final AtomicLong activatedSubscriptions = new AtomicLong(0);
    private final AtomicLong failedSubscriptions = new AtomicLong(0);
    public void apply(PaymentMetric metric) {
        if (metric.successful()) {
            successfulPayments.incrementAndGet();
        } else {
            failedPayments.incrementAndGet();
        }
    }
    public void apply(SubscriptionMetric metric) {
        if (metric.activated()) {
            activatedSubscriptions.incrementAndGet();
        } else {
            failedSubscriptions.incrementAndGet();
        }
    }
    public AnalyticsSummaryView view() {
        return new AnalyticsSummaryView(
                successfulPayments.get(),
                failedPayments.get(),
                activatedSubscriptions.get(),
                failedSubscriptions.get()
        );
    }
}

