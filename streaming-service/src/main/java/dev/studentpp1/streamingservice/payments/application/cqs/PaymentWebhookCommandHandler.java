package dev.studentpp1.streamingservice.payments.application.cqs;
import dev.studentpp1.streamingservice.payments.application.cqs.PaymentsCqs.*;
import dev.studentpp1.streamingservice.payments.application.usecase.PaymentWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class PaymentWebhookCommandHandler {
    private final PaymentWebhookService paymentWebhookService;
    public void handle(HandleStripeEventCommand command) {
        paymentWebhookService.handlePaymentEvent(command.event());
    }
}
