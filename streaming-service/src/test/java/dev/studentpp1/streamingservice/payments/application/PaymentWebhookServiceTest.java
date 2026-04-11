package dev.studentpp1.streamingservice.payments.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import dev.studentpp1.streamingservice.payments.application.usecase.PaymentWebhookService;
import dev.studentpp1.streamingservice.payments.domain.model.Payment;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import dev.studentpp1.streamingservice.payments.domain.model.vo.Money;
import dev.studentpp1.streamingservice.payments.domain.port.PaymentCompletionHandler;
import dev.studentpp1.streamingservice.payments.domain.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentWebhookServiceTest {

    private PaymentRepository paymentRepository;
    private PaymentCompletionHandler paymentCompletionHandler;
    private PaymentWebhookService webhookService;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        paymentCompletionHandler = mock(PaymentCompletionHandler.class);
        webhookService = new PaymentWebhookService(
                paymentRepository, paymentCompletionHandler, new ObjectMapper());
    }

    @Test
    void handlePaymentEvent_unknownType_noRepositoryInteraction() {
        Event event = mock(Event.class);
        when(event.getType()).thenReturn("payment.intent.created");

        webhookService.handlePaymentEvent(event);

        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(paymentCompletionHandler);
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
        when(paymentCompletionHandler.handleSuccess(2L, "Premium Plan", null))
                .thenReturn(10L);

        webhookService.handlePaymentEvent(event);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getPaidAt()).isNotNull();
        verify(paymentCompletionHandler).handleSuccess(2L, "Premium Plan", null);
        verify(paymentRepository).saveWithSubscription(payment, 10L);
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
        when(paymentCompletionHandler.handleSuccess(2L, "Basic", null))
                .thenReturn(11L);

        webhookService.handlePaymentEvent(event);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        verify(paymentCompletionHandler).handleSuccess(2L, "Basic", null);
        verify(paymentRepository).saveWithSubscription(payment, 11L);
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

        verifyNoInteractions(paymentCompletionHandler);
        verify(paymentRepository, never()).saveWithSubscription(any(), any());
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