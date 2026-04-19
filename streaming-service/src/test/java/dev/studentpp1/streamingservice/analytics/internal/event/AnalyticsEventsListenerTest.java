package dev.studentpp1.streamingservice.analytics.internal.event;

import dev.studentpp1.streamingservice.analytics.internal.acl.AnalyticsAclTranslator;
import dev.studentpp1.streamingservice.analytics.internal.projection.AnalyticsProjectionStore;
import dev.studentpp1.streamingservice.payments.api.event.PaymentSucceededEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyticsEventsListenerTest {

    @Test
    void updatesProjectionFromPublishedEvents() {
        AnalyticsProjectionStore store = new AnalyticsProjectionStore();
        AnalyticsEventsListener listener = new AnalyticsEventsListener(new AnalyticsAclTranslator(), store);

        listener.onPaymentSucceeded(new PaymentSucceededEvent(
                1L, 1L, "u@test.com", "Premium", "sess_1", BigDecimal.TEN, "USD", Instant.now()
        ));

        assertThat(store.view().successfulPayments()).isEqualTo(1);
        assertThat(store.view().failedPayments()).isEqualTo(0);
    }
}

