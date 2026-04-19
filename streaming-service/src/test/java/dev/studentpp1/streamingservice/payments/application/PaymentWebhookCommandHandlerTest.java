package dev.studentpp1.streamingservice.payments.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import dev.studentpp1.streamingservice.payments.application.command.webhook.PaymentWebhookCommandHandler;
import dev.studentpp1.streamingservice.payments.api.event.PaymentFailedEvent;
import dev.studentpp1.streamingservice.payments.api.event.PaymentSucceededEvent;
import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import dev.studentpp1.streamingservice.payments.domain.model.vo.Money;
import dev.studentpp1.streamingservice.payments.domain.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PaymentWebhookCommandHandlerTest {

    private static final Money DEFAULT_MONEY = new Money(BigDecimal.valueOf(9.99), "USD");

    private PaymentRepository paymentRepository;
    private ApplicationEventPublisher eventPublisher;
    private PaymentWebhookCommandHandler webhookService;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        webhookService = new PaymentWebhookCommandHandler(paymentRepository, eventPublisher, new ObjectMapper());
    }

    @Test
    void handlePaymentEvent_unknownType_noRepositoryInteraction() {
        Event event = mock(Event.class);
        when(event.getType()).thenReturn("payment.intent.created");

        webhookService.handlePaymentEvent(event);

        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void handlePaymentEvent_sessionExpired_marksPaymentAsFailed() {
        Payment payment = payment("sess_abc", PaymentStatus.PENDING, 1L, "Basic Plan", null);
        Event event = expiredEvent("sess_abc", 1L, "Basic Plan");

        when(paymentRepository.findByProviderSessionIdForUpdate("sess_abc"))
                .thenReturn(java.util.Optional.of(payment));

        webhookService.handlePaymentEvent(event);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        verify(eventPublisher).publishEvent(any(PaymentFailedEvent.class));
        verify(paymentRepository).save(payment);
    }

    @Test
    void handlePaymentEvent_sessionCompleted_marksPaymentAsPaidAndPublishesSuccessEvent() {
        Payment payment = payment("sess_xyz", PaymentStatus.PENDING, 2L, "Premium Plan", null);
        Event event = completedEvent("sess_xyz", 2L, "Premium Plan");

        when(paymentRepository.findByProviderSessionIdForUpdate("sess_xyz"))
                .thenReturn(java.util.Optional.of(payment));

        webhookService.handlePaymentEvent(event);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getUserSubscriptionId()).isNull();
        assertThat(payment.getPaidAt()).isNotNull();
        verify(eventPublisher).publishEvent(any(PaymentSucceededEvent.class));
        verify(paymentRepository).save(payment);
    }

    @Test
    void handlePaymentEvent_sessionCompleted_usesProductNameMetadataAsFallback() {
        Payment payment = Payment.restore(
                1L, "sess_product", PaymentStatus.PENDING,
                new Money(BigDecimal.valueOf(4.99), "USD"), LocalDateTime.now(), null, null, 2L, "Basic");
        Event event = completedEventWithProductName("sess_product", 2L, "Basic");

        when(paymentRepository.findByProviderSessionIdForUpdate("sess_product"))
                .thenReturn(java.util.Optional.of(payment));

        webhookService.handlePaymentEvent(event);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        verify(eventPublisher).publishEvent(any(PaymentSucceededEvent.class));
        verify(paymentRepository).save(payment);
    }

    @Test
    void handlePaymentEvent_sessionCompleted_alreadyCompleted_skipsProcessing() {
        Payment payment = payment("sess_done", PaymentStatus.COMPLETED, 1L, "Basic Plan", 5L);
        Event event = completedEvent("sess_done", 1L, "Basic Plan");

        when(paymentRepository.findByProviderSessionIdForUpdate("sess_done"))
                .thenReturn(java.util.Optional.of(payment));

        webhookService.handlePaymentEvent(event);

        verifyNoInteractions(eventPublisher);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void handlePaymentEvent_sessionCompleted_syncSubscriberFails_propagatesException() {
        Payment payment = payment("sess_sync_fail", PaymentStatus.PENDING, 2L, "Premium Plan", null);
        Event event = completedEvent("sess_sync_fail", 2L, "Premium Plan");

        when(paymentRepository.findByProviderSessionIdForUpdate("sess_sync_fail"))
                .thenReturn(java.util.Optional.of(payment));
        doThrow(new RuntimeException("subscription sync handler failed"))
                .when(eventPublisher).publishEvent(any(PaymentSucceededEvent.class));

        assertThatThrownBy(() -> webhookService.handlePaymentEvent(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("sync handler failed");
    }

    @Test
    void handlePaymentEvent_sessionExpired_alreadyFailed_skipsDuplicateProcessing() {
        Payment payment = payment("sess_failed", PaymentStatus.FAILED, 1L, "Basic Plan", null);
        Event event = expiredEvent("sess_failed", 1L, "Basic Plan");

        when(paymentRepository.findByProviderSessionIdForUpdate("sess_failed"))
                .thenReturn(java.util.Optional.of(payment));

        webhookService.handlePaymentEvent(event);

        verifyNoInteractions(eventPublisher);
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

    private Payment payment(String sessionId, PaymentStatus status, Long userId, String planName, Long subscriptionId) {
        LocalDateTime paidAt = status == PaymentStatus.COMPLETED ? LocalDateTime.now() : null;
        return Payment.restore(
                1L,
                sessionId,
                status,
                DEFAULT_MONEY,
                LocalDateTime.now(),
                paidAt,
                subscriptionId,
                userId,
                planName
        );
    }

    private Event completedEvent(String sessionId, long userId, String planName) {
        return buildMockEvent("checkout.session.completed", metadataPayload(sessionId, userId, "planName", planName));
    }

    private Event completedEventWithProductName(String sessionId, long userId, String productName) {
        return buildMockEvent("checkout.session.completed", metadataPayload(sessionId, userId, "productName", productName));
    }

    private Event expiredEvent(String sessionId, long userId, String planName) {
        return buildMockEvent("checkout.session.expired", metadataPayload(sessionId, userId, "planName", planName));
    }

    private String metadataPayload(String sessionId, long userId, String valueKey, String value) {
        return """
                {
                    "id": "%s",
                    "metadata": {
                        "userId": "%s",
                        "%s": "%s"
                    }
                }
                """.formatted(sessionId, userId, valueKey, value);
    }
}