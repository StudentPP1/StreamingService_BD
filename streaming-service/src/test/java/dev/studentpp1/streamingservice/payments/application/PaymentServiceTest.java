package dev.studentpp1.streamingservice.payments.application;

import dev.studentpp1.streamingservice.payments.application.usecase.PaymentService;
import dev.studentpp1.streamingservice.payments.domain.factory.PaymentFactory;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentHistoryItem;
import dev.studentpp1.streamingservice.payments.domain.model.PaymentStatus;
import dev.studentpp1.streamingservice.payments.domain.repository.PaymentRepository;
import dev.studentpp1.streamingservice.payments.presentation.dto.HistoryPaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private PaymentRepository paymentRepository;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        PaymentFactory paymentFactory = mock(PaymentFactory.class);
        paymentService = new PaymentService(paymentRepository, paymentFactory);

        ReflectionTestUtils.setField(paymentService, "secretKey", "sk_test_dummy");
        ReflectionTestUtils.setField(paymentService, "successUrl", "http://localhost/success");
        ReflectionTestUtils.setField(paymentService, "cancelUrl", "http://localhost/cancel");
        ReflectionTestUtils.setField(paymentService, "currency", "USD");
    }

    @Test
    void getUserPayments_delegatesToRepository() {
        PaymentHistoryItem response = new PaymentHistoryItem(
                PaymentStatus.COMPLETED, LocalDateTime.now(),
                BigDecimal.valueOf(9.99), "Basic Plan");

        when(paymentRepository.getPaymentByUserId(1L)).thenReturn(List.of(response));

        List<HistoryPaymentResponse> result = paymentService.getUserPayments(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).subscriptionName()).isEqualTo("Basic Plan");
        verify(paymentRepository).getPaymentByUserId(1L);
    }

    @Test
    void getUserPayments_noPayments_returnsEmptyList() {
        when(paymentRepository.getPaymentByUserId(99L)).thenReturn(List.of());

        List<HistoryPaymentResponse> result = paymentService.getUserPayments(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void getPaymentsBySubscription_delegatesToRepository() {
        PaymentHistoryItem response = new PaymentHistoryItem(
                PaymentStatus.COMPLETED, LocalDateTime.now(),
                BigDecimal.valueOf(19.99), "Premium Plan");

        when(paymentRepository.getPaymentByUserSubscription(1L, 5L)).thenReturn(List.of(response));

        List<HistoryPaymentResponse> result = paymentService.getPaymentsBySubscription(1L, 5L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).subscriptionName()).isEqualTo("Premium Plan");
        verify(paymentRepository).getPaymentByUserSubscription(1L, 5L);
    }

    @Test
    void deleteOldPayments_deletesPaymentsOlderThanOneYear() {
        when(paymentRepository.deletePaymentsBefore(any(LocalDateTime.class))).thenReturn(3);

        paymentService.deleteOldPayments();

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(paymentRepository).deletePaymentsBefore(captor.capture());

        LocalDateTime threshold = captor.getValue();
        assertThat(threshold).isBefore(LocalDateTime.now().minusMonths(11));
    }
}