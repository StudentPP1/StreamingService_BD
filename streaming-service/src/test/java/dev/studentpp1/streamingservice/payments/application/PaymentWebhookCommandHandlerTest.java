package dev.studentpp1.streamingservice.payments.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import dev.studentpp1.streamingservice.payments.application.command.webhook.PaymentWebhookCommandHandler;
import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import dev.studentpp1.streamingservice.payments.domain.model.vo.Money;
import dev.studentpp1.streamingservice.payments.domain.port.SubscriptionAfterPaymentPort;
import dev.studentpp1.streamingservice.payments.domain.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PaymentWebhookCommandHandlerTest {

    private PaymentRepository paymentRepository;
    private SubscriptionAfterPaymentPort subscriptionPort;
    private PaymentWebhookCommandHandler webhookService;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        subscriptionPort = mock(SubscriptionAfterPaymentPort.class);
        webhookService = new PaymentWebhookCommandHandler(paymentRepository, subscriptionPort, new ObjectMapper());
    }

    @Test
    void handlePaymentEvent_unknownType_noRepositoryInteraction() {
        Event event = mock(Event.class);
        when(event.getType()).thenReturn("payment.intent.created");

        webhookService.handlePaymentEvent(event);

        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(subscriptionPort);
    }

    @Test
    void handlePaymentEvent_sessionExpired_marksPaymentAsFailed() {
        Money money = new Money(BigDecimal.valueOf(9.99), "USD");
        Payment payment = Payment.restore(
                1L, "sess_abc", PaymentStatus.PENDING,
                money, LocalDateTime.now(), null, null, 1L, "Basic Plan");

        Event event = buildMockEvent(
                "checkout.session.expired",
                """
                {
                    "id": "sess_abc",
                    "metadata": {
                        "userId": "1",
                        "planName": "Basic Plan"
                    }
                }
                """
        );

        when(paymentRepository.findByProviderSessionIdForUpdate("sess_abc"))
                .thenReturn(java.util.Optional.of(payment));

        webhookService.handlePaymentEvent(event);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        verify(subscriptionPort).onPaymentFailed(eq(1L), any(), eq("Basic Plan"), isNull(), anyString());
        verify(paymentRepository).save(payment);
    }

    @Test
    void handlePaymentEvent_sessionCompleted_marksPaymentAsPaidAndLinksSubscription() {
        Money money = new Money(BigDecimal.valueOf(9.99), "USD");
        Payment payment = Payment.restore(
                1L, "sess_xyz", PaymentStatus.PENDING,
                money, LocalDateTime.now(), null, null, 2L, "Premium Plan");

        Event event = buildMockEvent(
                "checkout.session.completed",
                """
                {
                    "id": "sess_xyz",
                    "metadata": {
                        "userId": "2",
                        "planName": "Premium Plan"
                    }
                }
                """
        );

        when(paymentRepository.findByProviderSessionIdForUpdate("sess_xyz"))
                .thenReturn(java.util.Optional.of(payment));
        when(subscriptionPort.onPaymentSucceeded(1L, 2L, null, "Premium Plan")).thenReturn(44L);

        webhookService.handlePaymentEvent(event);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getUserSubscriptionId()).isEqualTo(44L);
        assertThat(payment.getPaidAt()).isNotNull();
        verify(paymentRepository).save(payment);
    }

    @Test
    void handlePaymentEvent_sessionCompleted_usesProductNameMetadataAsFallback() {
        Money money = new Money(BigDecimal.valueOf(4.99), "USD");
        Payment payment = Payment.restore(
                1L, "sess_product", PaymentStatus.PENDING,
                money, LocalDateTime.now(), null, null, 2L, "Basic");

        Event event = buildMockEvent(
                "checkout.session.completed",
                """
                {
                    "id": "sess_product",
                    "metadata": {
                        "userId": "2",
                        "productName": "Basic"
                    }
                }
                """
        );

        when(paymentRepository.findByProviderSessionIdForUpdate("sess_product"))
                .thenReturn(java.util.Optional.of(payment));
        when(subscriptionPort.onPaymentSucceeded(1L, 2L, null, "Basic")).thenReturn(55L);

        webhookService.handlePaymentEvent(event);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getUserSubscriptionId()).isEqualTo(55L);
        verify(paymentRepository).save(payment);
    }

    @Test
    void handlePaymentEvent_sessionCompleted_alreadyCompleted_skipsProcessing() {
        Money money = new Money(BigDecimal.valueOf(9.99), "USD");
        Payment payment = Payment.restore(
                1L, "sess_done", PaymentStatus.COMPLETED,
                money, LocalDateTime.now(), LocalDateTime.now(), 5L, 1L, "Basic Plan");

        Event event = buildMockEvent(
                "checkout.session.completed",
                """
                {
                    "id": "sess_done",
                    "metadata": {
                        "userId": "1",
                        "planName": "Basic Plan"
                    }
                }
                """
        );

        when(paymentRepository.findByProviderSessionIdForUpdate("sess_done"))
                .thenReturn(java.util.Optional.of(payment));

        webhookService.handlePaymentEvent(event);

        verifyNoInteractions(subscriptionPort);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void handlePaymentEvent_sessionCompleted_subscriptionPortFails_propagatesException() {
        Money money = new Money(BigDecimal.valueOf(9.99), "USD");
        Payment payment = Payment.restore(
                1L, "sess_sync_fail", PaymentStatus.PENDING,
                money, LocalDateTime.now(), null, null, 2L, "Premium Plan");

        Event event = buildMockEvent(
                "checkout.session.completed",
                """
                {
                    "id": "sess_sync_fail",
                    "metadata": {
                        "userId": "2",
                        "planName": "Premium Plan"
                    }
                }
                """
        );

        when(paymentRepository.findByProviderSessionIdForUpdate("sess_sync_fail"))
                .thenReturn(java.util.Optional.of(payment));
        when(subscriptionPort.onPaymentSucceeded(anyLong(), anyLong(), any(), anyString()))
                .thenThrow(new RuntimeException("subscription creation failed"));

        assertThatThrownBy(() -> webhookService.handlePaymentEvent(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("subscription creation failed");
    }

    @Test
    void handlePaymentEvent_sessionExpired_alreadyFailed_skipsDuplicateProcessing() {
        Money money = new Money(BigDecimal.valueOf(9.99), "USD");
        Payment payment = Payment.restore(
                1L, "sess_failed", PaymentStatus.FAILED,
                money, LocalDateTime.now(), null, null, 1L, "Basic Plan");

        Event event = buildMockEvent(
                "checkout.session.expired",
                """
                {
                    "id": "sess_failed",
                    "metadata": {
                        "userId": "1",
                        "planName": "Basic Plan"
                    }
                }
                """
        );

        when(paymentRepository.findByProviderSessionIdForUpdate("sess_failed"))
                .thenReturn(java.util.Optional.of(payment));

        webhookService.handlePaymentEvent(event);

        verifyNoInteractions(subscriptionPort);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void deleteStalePendingPayments_callsRepositoryWithPendingStatusAndThreshold() {
        when(paymentRepository.deleteByStatusAndCreatedAtBefore(
                eq(PaymentStatus.PENDING), any(LocalDateTime.class))).thenReturn(2);

        webhookService.deleteStalePendingPayments();

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(paymentRepository).deleteByStatusAndCreatedAtBefore(
                eq(PaymentStatus.PENDING), captor.capture());

        LocalDateTime threshold = captor.getValue();
        assertThat(threshold).isBefore(LocalDateTime.now().minusHours(23));
    }

    private Event buildMockEvent(String type, String rawJson) {
        Event event = mock(Event.class);
        when(event.getType()).thenReturn(type);

        com.stripe.model.EventDataObjectDeserializer deserializer =
                mock(com.stripe.model.EventDataObjectDeserializer.class);
        when(deserializer.getRawJson()).thenReturn(rawJson);
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);

        return event;
    }
}
