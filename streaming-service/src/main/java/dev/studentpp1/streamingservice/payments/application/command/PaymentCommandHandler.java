package dev.studentpp1.streamingservice.payments.application.command;

import dev.studentpp1.streamingservice.payments.application.command.checkout.CheckoutPaymentHandler;
import dev.studentpp1.streamingservice.payments.application.command.webhook.HandlePaymentWebhookCommand;
import dev.studentpp1.streamingservice.payments.application.command.webhook.PaymentWebhookCommandHandler;
import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentRequest;
import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCommandHandler {

    private final CheckoutPaymentHandler checkoutPaymentHandler;
    private final PaymentWebhookCommandHandler paymentWebhookCommandHandler;

    public CheckoutPaymentResponse handle(CheckoutPaymentRequest command) {
        return checkoutPaymentHandler.checkout(command);
    }

    public void handle(HandlePaymentWebhookCommand command) {
        paymentWebhookCommandHandler.handle(command);
    }
}

