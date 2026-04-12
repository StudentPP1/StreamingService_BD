package dev.studentpp1.streamingservice.payments.application.query.history;

import java.util.List;

public interface PaymentReadRepository {

    List<PaymentHistoryReadModel> findByUserId(Long userId);

    List<PaymentHistoryReadModel> findByUserSubscription(Long userId, Long userSubscriptionId);
}

