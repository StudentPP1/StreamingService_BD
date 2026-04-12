package dev.studentpp1.streamingservice.payments.application.query;

import dev.studentpp1.streamingservice.payments.application.dto.HistoryPaymentResponse;
import dev.studentpp1.streamingservice.payments.application.usecase.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentQueryHandler {

    private final PaymentService paymentService;

    public List<HistoryPaymentResponse> handle(GetUserPaymentsQuery query) {
        return paymentService.getUserPayments(query.userId());
    }

    public List<HistoryPaymentResponse> handle(GetSubscriptionPaymentsQuery query) {
        return paymentService.getPaymentsBySubscription(query.userId(), query.subscriptionId());
    }
}

