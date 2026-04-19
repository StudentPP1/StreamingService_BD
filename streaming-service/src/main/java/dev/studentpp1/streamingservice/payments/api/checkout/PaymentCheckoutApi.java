package dev.studentpp1.streamingservice.payments.api.checkout;

public interface PaymentCheckoutApi {
    PaymentCheckoutResponse checkout(PaymentCheckoutRequest request);
}

