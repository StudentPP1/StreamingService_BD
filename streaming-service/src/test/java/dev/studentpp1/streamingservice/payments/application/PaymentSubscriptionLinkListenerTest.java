package dev.studentpp1.streamingservice.payments.application;

import dev.studentpp1.streamingservice.payments.application.event.listener.PaymentSubscriptionLinkListener;
import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import dev.studentpp1.streamingservice.payments.domain.model.vo.Money;
import dev.studentpp1.streamingservice.payments.domain.repository.PaymentRepository;
import dev.studentpp1.streamingservice.subscription.api.event.SubscriptionLinkedToPaymentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PaymentSubscriptionLinkListenerTest {

    private static final Money DEFAULT_MONEY = new Money(BigDecimal.valueOf(9.99), "USD");

    private PaymentRepository paymentRepository;
    private PaymentSubscriptionLinkListener listener;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        listener = new PaymentSubscriptionLinkListener(paymentRepository);
    }

    @Test
    void onSubscriptionLinked_assignsSubscriptionIdAndSavesPayment() {
        Payment payment = completedPayment("sess_1", null);
        when(paymentRepository.findByProviderSessionIdForUpdate("sess_1")).thenReturn(Optional.of(payment));

        listener.onSubscriptionLinked(new SubscriptionLinkedToPaymentEvent(5L, "sess_1", 44L, Instant.now()));

        assertThat(payment.getUserSubscriptionId()).isEqualTo(44L);
        verify(paymentRepository).save(payment);
    }

    @Test
    void onSubscriptionLinked_sameLinkTwice_isIdempotent() {
        Payment payment = completedPayment("sess_1", 44L);
        when(paymentRepository.findByProviderSessionIdForUpdate("sess_1")).thenReturn(Optional.of(payment));

        listener.onSubscriptionLinked(new SubscriptionLinkedToPaymentEvent(5L, "sess_1", 44L, Instant.now()));

        verify(paymentRepository, never()).save(any());
    }

    private Payment completedPayment(String sessionId, Long subscriptionId) {
        return Payment.restore(
                5L,
                sessionId,
                PaymentStatus.COMPLETED,
                DEFAULT_MONEY,
                LocalDateTime.now(),
                LocalDateTime.now(),
                subscriptionId,
                1L,
                "Premium"
        );
    }
}

