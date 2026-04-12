package dev.studentpp1.streamingservice.payments.infrastructure.adapter;

import dev.studentpp1.streamingservice.payments.domain.port.PaymentCompletionHandler;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionActivationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionPaymentHandlerAdapter implements PaymentCompletionHandler {

    private final SubscriptionActivationPort subscriptionActivationPort;

    @Override
    public Long handleSuccess(Long userId, String planName, List<String> emails) {
        if (emails != null && !emails.isEmpty()) {
            var subs = subscriptionActivationPort.createFamilySubscriptionAfterPayment(userId, planName, emails);
            return subs.getFirst().getId();
        } else {
            var sub = subscriptionActivationPort.createUserSubscription(planName, userId);
            return sub.getId();
        }
    }
}
