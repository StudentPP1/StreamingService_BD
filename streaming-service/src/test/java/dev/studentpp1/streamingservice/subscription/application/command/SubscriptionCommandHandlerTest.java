package dev.studentpp1.streamingservice.subscription.application.command;

import dev.studentpp1.streamingservice.subscription.application.command.subscription.CancelSubscriptionCommand;
import dev.studentpp1.streamingservice.subscription.application.command.subscription.CancelSubscriptionHandler;
import dev.studentpp1.streamingservice.subscription.application.command.subscription.SubscribeUserCommand;
import dev.studentpp1.streamingservice.subscription.application.command.subscription.SubscribeUserHandler;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SubscriptionCommandHandlerTest {

    @Test
    void commandMethods_delegateToSpecificHandlers() {
        SubscribeUserHandler subscribeUserHandler = mock(SubscribeUserHandler.class);
        CancelSubscriptionHandler cancelSubscriptionHandler = mock(CancelSubscriptionHandler.class);

        SubscriptionCommandHandler handler = new SubscriptionCommandHandler(
                subscribeUserHandler,
                cancelSubscriptionHandler
        );

        SubscribeUserCommand subscribeCommand = new SubscribeUserCommand(1L, 2L, "user@test.com");
        CheckoutResult checkoutResult = new CheckoutResult("PENDING", "ok", "sess", "url");
        when(subscribeUserHandler.handle(subscribeCommand)).thenReturn(checkoutResult);

        assertThat(handler.handle(subscribeCommand)).isEqualTo(checkoutResult);

        CancelSubscriptionCommand cancelCommand = new CancelSubscriptionCommand(5L, 2L);
        handler.handle(cancelCommand);
        verify(cancelSubscriptionHandler).handle(cancelCommand);
    }
}

