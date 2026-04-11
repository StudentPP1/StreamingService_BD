package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentCommand;
import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentResult;
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
        CheckoutPaymentResult response = paymentCheckoutGateway.checkout(new CheckoutPaymentCommand(
                command.productName(),
                command.price(),
                command.userId(),
                command.metadata()
        ));
        return mapToCheckoutResult(response);
    }

    private CheckoutResult mapToCheckoutResult(CheckoutPaymentResult response) {
        return new CheckoutResult(
                response.status(),
                response.message(),
                response.sessionId(),
                response.sessionUrl()
        );
    }
}