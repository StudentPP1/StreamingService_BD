package dev.studentpp1.streamingservice.payments.application.cqs;
import dev.studentpp1.streamingservice.payments.application.cqs.PaymentsCqs.*;
import dev.studentpp1.streamingservice.payments.application.usecase.PaymentService;
import dev.studentpp1.streamingservice.payments.presentation.dto.HistoryPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class PaymentQueryHandler {
    private final PaymentService paymentService;
    public List<HistoryPaymentResponse> handle(GetUserPaymentsQuery query) {
        return paymentService.getUserPayments(query.userId());
    }
    public List<HistoryPaymentResponse> handle(GetPaymentsBySubscriptionQuery query) {
        return paymentService.getPaymentsBySubscription(query.userId(), query.subscriptionId());
    }
}
