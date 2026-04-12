package dev.studentpp1.streamingservice.payments.domain.port;

import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentRequest;
import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentResponse;

public interface PaymentCheckoutGateway {
    CheckoutPaymentResponse checkout(CheckoutPaymentRequest command);
}

