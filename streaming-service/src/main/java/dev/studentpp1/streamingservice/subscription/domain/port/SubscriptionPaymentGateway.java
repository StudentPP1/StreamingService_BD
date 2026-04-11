package dev.studentpp1.streamingservice.subscription.domain.port;

import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutCommand;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;

public interface SubscriptionPaymentGateway {
    CheckoutResult generateCheckout(CheckoutCommand command);
}