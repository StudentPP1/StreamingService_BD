package dev.studentpp1.streamingservice.analytics.internal.acl;

import dev.studentpp1.streamingservice.payments.api.event.PaymentFailedEvent;
import dev.studentpp1.streamingservice.payments.api.event.PaymentSucceededEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionActivatedEvent;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionFailedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyticsAclTranslatorTest {

    private final AnalyticsAclTranslator translator = new AnalyticsAclTranslator();

    @Test
    void mapsPaymentEventsToInternalMetrics() {
        assertThat(translator.toPaymentMetric(new PaymentSucceededEvent(
                1L, 1L, "u@test.com", "Premium", "sess_1", BigDecimal.TEN, "USD", Instant.now()
        )).successful()).isTrue();

        assertThat(translator.toPaymentMetric(new PaymentFailedEvent(
                2L, 1L, "u@test.com", "Premium", "sess_2", null, "failed", BigDecimal.TEN, "USD", Instant.now()
        )).successful()).isFalse();
    }

    @Test
    void mapsSubscriptionEventsToInternalMetrics() {
        assertThat(translator.toSubscriptionMetric(new SubscriptionActivatedEvent(
                1L, 1L, "u@test.com", "Premium", LocalDateTime.now().plusDays(30), Instant.now()
        )).activated()).isTrue();

        assertThat(translator.toSubscriptionMetric(new SubscriptionFailedEvent(
                1L, "u@test.com", "Premium", "failed", Instant.now()
        )).activated()).isFalse();
    }
}

