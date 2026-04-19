package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentRequest;
import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentResponse;
import dev.studentpp1.streamingservice.payments.domain.port.PaymentCheckoutGateway;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutCommand;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionPaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentServiceAdapter implements SubscriptionPaymentGateway {

    private final PaymentCheckoutGateway paymentCheckoutGateway;

    @Override
    public CheckoutResult generateCheckout(CheckoutCommand command) {
        CheckoutPaymentResponse response = paymentCheckoutGateway.checkout(new CheckoutPaymentRequest(
                command.productName(),
                command.price(),
                command.userId(),
                command.userEmail()
        ));
        return mapToCheckoutResult(response);
    }

    private CheckoutResult mapToCheckoutResult(CheckoutPaymentResponse response) {
        return new CheckoutResult(
                response.status(),
                response.message(),
                response.sessionId(),
                response.sessionUrl()
        );
    }
}