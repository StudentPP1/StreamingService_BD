package dev.studentpp1.streamingservice.subscription.internal.acl;

import dev.studentpp1.streamingservice.payments.api.checkout.PaymentCheckoutRequest;
import dev.studentpp1.streamingservice.payments.api.checkout.PaymentCheckoutResponse;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutCommand;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionPaymentsAclTranslator {

    public PaymentCheckoutRequest toExternalRequest(CheckoutCommand command) {
        return new PaymentCheckoutRequest(
                command.productName(),
                command.price(),
                command.userId(),
                command.userEmail()
        );
    }

    public CheckoutResult toInternalResult(PaymentCheckoutResponse result) {
        return new CheckoutResult(
                result.status(),
                result.message(),
                result.sessionId(),
                result.sessionUrl()
        );
    }
}


