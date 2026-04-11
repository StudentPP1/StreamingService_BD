package dev.studentpp1.streamingservice.payments.application.read;

import dev.studentpp1.streamingservice.payments.presentation.dto.HistoryPaymentResponse;

import java.util.List;

public interface PaymentsReadRepository {
    List<HistoryPaymentResponse> findUserPayments(Long userId);

    List<HistoryPaymentResponse> findUserPaymentsBySubscription(Long userId, Long subscriptionId);
}

