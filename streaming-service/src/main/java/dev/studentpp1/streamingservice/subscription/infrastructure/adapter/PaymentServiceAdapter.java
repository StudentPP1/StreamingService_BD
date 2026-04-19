package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.payments.api.checkout.PaymentCheckoutApi;
import dev.studentpp1.streamingservice.payments.api.checkout.PaymentCheckoutResponse;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutCommand;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionPaymentGateway;
import dev.studentpp1.streamingservice.subscription.internal.acl.SubscriptionPaymentsAclTranslator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentServiceAdapter implements SubscriptionPaymentGateway {

    private final PaymentCheckoutApi paymentCheckoutApi;
    private final SubscriptionPaymentsAclTranslator aclTranslator;

    @Override
    public CheckoutResult generateCheckout(CheckoutCommand command) {
        PaymentCheckoutResponse response = paymentCheckoutApi.checkout(aclTranslator.toExternalRequest(command));
        return aclTranslator.toInternalResult(response);
    }
}