package dev.studentpp1.streamingservice.payments.application.cqs;

import dev.studentpp1.streamingservice.payments.application.cqs.PaymentsCqs.*;
import dev.studentpp1.streamingservice.payments.application.read.PaymentsReadRepository;
import dev.studentpp1.streamingservice.payments.presentation.dto.HistoryPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentQueryHandler {
    private final PaymentsReadRepository paymentsReadRepository;

    public List<HistoryPaymentResponse> handle(GetUserPaymentsQuery query) {
        return paymentsReadRepository.findUserPayments(query.userId());
    }

    public List<HistoryPaymentResponse> handle(GetPaymentsBySubscriptionQuery query) {
        return paymentsReadRepository.findUserPaymentsBySubscription(query.userId(), query.subscriptionId());
    }
}
