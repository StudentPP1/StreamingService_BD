package dev.studentpp1.streamingservice.payments.application.cqs;

import com.stripe.model.Event;
import dev.studentpp1.streamingservice.payments.application.usecase.PaymentWebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentWebhookCommandHandlerUnitTest {
    @Mock
    private PaymentWebhookService paymentWebhookService;
    private PaymentWebhookCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new PaymentWebhookCommandHandler(paymentWebhookService);
    }

    @Test
    void handle_delegatesEventToService() {
        Event event = mock(Event.class);
        handler.handle(new PaymentsCqs.HandleStripeEventCommand(event));
        verify(paymentWebhookService).handlePaymentEvent(event);
    }
}

