package dev.studentpp1.streamingservice.payments.application;

import com.stripe.model.Event;
import dev.studentpp1.streamingservice.payments.application.command.PaymentCommandHandler;
import dev.studentpp1.streamingservice.payments.application.command.checkout.CheckoutPaymentHandler;
import dev.studentpp1.streamingservice.payments.application.command.webhook.HandlePaymentWebhookCommand;
import dev.studentpp1.streamingservice.payments.application.command.webhook.PaymentWebhookCommandHandler;
import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentRequest;
import dev.studentpp1.streamingservice.payments.domain.model.CheckoutPaymentResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PaymentCommandHandlerTest {

    @Test
    void handleCheckout_delegatesToCheckoutHandler() {
        CheckoutPaymentHandler checkoutPaymentHandler = mock(CheckoutPaymentHandler.class);
        PaymentWebhookCommandHandler webhookCommandHandler = mock(PaymentWebhookCommandHandler.class);
        PaymentCommandHandler commandHandler = new PaymentCommandHandler(checkoutPaymentHandler, webhookCommandHandler);

        CheckoutPaymentRequest command = new CheckoutPaymentRequest("Basic", BigDecimal.TEN, 1L, Map.of());
        CheckoutPaymentResponse expected = new CheckoutPaymentResponse("PENDING", "ok", "sess", "url");

        when(checkoutPaymentHandler.checkout(command)).thenReturn(expected);

        CheckoutPaymentResponse actual = commandHandler.handle(command);

        assertThat(actual).isEqualTo(expected);
        verify(checkoutPaymentHandler).checkout(command);
        verifyNoInteractions(webhookCommandHandler);
    }

    @Test
    void handleWebhook_delegatesToWebhookHandler() {
        CheckoutPaymentHandler checkoutPaymentHandler = mock(CheckoutPaymentHandler.class);
        PaymentWebhookCommandHandler webhookCommandHandler = mock(PaymentWebhookCommandHandler.class);
        PaymentCommandHandler commandHandler = new PaymentCommandHandler(checkoutPaymentHandler, webhookCommandHandler);

        Event event = mock(Event.class);
        HandlePaymentWebhookCommand command = new HandlePaymentWebhookCommand(event);

        commandHandler.handle(command);

        verify(webhookCommandHandler).handle(command);
        verifyNoInteractions(checkoutPaymentHandler);
    }
}

