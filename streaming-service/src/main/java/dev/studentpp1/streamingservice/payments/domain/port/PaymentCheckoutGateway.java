package dev.studentpp1.streamingservice.payments.domain.port;

import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentCommand;
import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentResult;

public interface PaymentCheckoutGateway {
    CheckoutPaymentResult checkout(CheckoutPaymentCommand command);
}

