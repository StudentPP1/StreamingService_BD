package dev.studentpp1.streamingservice.payments.infrastructure.adapter;

import dev.studentpp1.streamingservice.payments.application.command.checkout.CheckoutPaymentHandler;
import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentRequest;
import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentResponse;
import dev.studentpp1.streamingservice.payments.domain.port.PaymentCheckoutGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCheckoutGatewayAdapter implements PaymentCheckoutGateway {

    private final CheckoutPaymentHandler checkoutPaymentHandler;

    @Override
    public CheckoutPaymentResponse checkout(CheckoutPaymentRequest command) {
        return checkoutPaymentHandler.checkout(command);
    }
}

