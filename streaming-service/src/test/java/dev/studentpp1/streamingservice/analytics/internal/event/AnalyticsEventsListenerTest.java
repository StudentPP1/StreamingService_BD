package dev.studentpp1.streamingservice.analytics.internal.event;

import dev.studentpp1.streamingservice.analytics.internal.acl.AnalyticsAclTranslator;
import dev.studentpp1.streamingservice.analytics.internal.impl.AnalyticsData;
import dev.studentpp1.streamingservice.common.event.EventBus;
import dev.studentpp1.streamingservice.payments.api.event.PaymentSucceededEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AnalyticsEventsListenerTest {

    @Test
    void updatesProjectionFromPublishedEvents() {
        AnalyticsData store = new AnalyticsData();
        AnalyticsEventsListener listener = new AnalyticsEventsListener(mock(EventBus.class), new AnalyticsAclTranslator(), store);

        listener.onPaymentSucceeded(new PaymentSucceededEvent(
                1L, 1L, "u@test.com", "Premium", "sess_1", BigDecimal.TEN, "USD", Instant.now()
        ));

        assertThat(store.view().successfulPayments()).isEqualTo(1);
        assertThat(store.view().failedPayments()).isEqualTo(0);
    }
}

