package dev.studentpp1.streamingservice.subscription.application.adapter;

import dev.studentpp1.streamingservice.payments.domain.port.PaymentCompletionHandler;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionPaymentHandlerAdapter implements PaymentCompletionHandler {

    private final SubscriptionService subscriptionService;

    @Override
    public Long handleSuccess(Long userId, String planName, List<String> emails) {
        if (emails != null && !emails.isEmpty()) {
            var subs = subscriptionService.createFamilySubscriptionAfterPayment(userId, planName, emails);
            return subs.getFirst().getId();
        } else {
            var sub = subscriptionService.createUserSubscription(planName, userId);
            return sub.getId();
        }
    }
}
