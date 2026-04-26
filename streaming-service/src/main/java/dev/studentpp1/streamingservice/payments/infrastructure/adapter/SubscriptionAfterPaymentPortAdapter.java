package dev.studentpp1.streamingservice.payments.infrastructure.adapter;

import dev.studentpp1.streamingservice.payments.domain.port.SubscriptionAfterPaymentPort;
import dev.studentpp1.streamingservice.subscription.api.payment.SubscriptionAfterPaymentApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("paymentSubscriptionAfterPaymentPortAdapter")
@RequiredArgsConstructor
public class SubscriptionAfterPaymentPortAdapter implements SubscriptionAfterPaymentPort {

    private final SubscriptionAfterPaymentApi subscriptionAfterPaymentApi;

    @Override
    public Long onPaymentSucceeded(Long paymentId, Long userId, String userEmail, String planName) {
        return subscriptionAfterPaymentApi.onPaymentSucceeded(paymentId, userId, userEmail, planName);
    }

    @Override
    public void onPaymentFailed(Long userId, String userEmail, String planName, Long existingSubscriptionId, String reason) {
        subscriptionAfterPaymentApi.onPaymentFailed(userId, userEmail, planName, existingSubscriptionId, reason);
    }
}

