package dev.studentpp1.streamingservice.payments.application.query.history;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentQueryHandler {

    private final PaymentReadRepository paymentReadRepository;

    public List<PaymentHistoryReadModel> handle(GetUserPaymentsQuery query) {
        return paymentReadRepository.findByUserId(query.userId());
    }

    public List<PaymentHistoryReadModel> handle(GetSubscriptionPaymentsQuery query) {
        return paymentReadRepository.findByUserSubscription(query.userId(), query.subscriptionId());
    }
}

