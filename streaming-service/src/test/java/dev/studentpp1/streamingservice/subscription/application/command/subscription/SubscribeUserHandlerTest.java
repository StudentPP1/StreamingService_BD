package dev.studentpp1.streamingservice.subscription.application.command.subscription;

import dev.studentpp1.streamingservice.subscription.domain.exception.ActiveSubscriptionAlreadyExistsException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutCommand;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionPaymentGateway;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SubscribeUserHandlerTest {

    private SubscriptionPlanRepository subscriptionPlanRepository;
    private UserSubscriptionRepository userSubscriptionRepository;
    private SubscriptionPaymentGateway paymentGateway;
    private SubscribeUserHandler handler;

    @BeforeEach
    void setUp() {
        subscriptionPlanRepository = mock(SubscriptionPlanRepository.class);
        userSubscriptionRepository = mock(UserSubscriptionRepository.class);
        paymentGateway = mock(SubscriptionPaymentGateway.class);
        handler = new SubscribeUserHandler(subscriptionPlanRepository, userSubscriptionRepository, paymentGateway);
    }

    @Test
    void handle_planNotFound_throwsSubscriptionPlanNotFoundException() {
        when(subscriptionPlanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new SubscribeUserCommand(99L, 1L, "user@test.com")))
                .isInstanceOf(SubscriptionPlanNotFoundException.class);

        verifyNoInteractions(paymentGateway);
    }

    @Test
    void handle_activeSubscriptionExists_throwsActiveSubscriptionAlreadyExistsException() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                10L, "Basic", "desc", BigDecimal.valueOf(9.99), 30, Set.of(), 0L);
        when(subscriptionPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(userSubscriptionRepository.existsByUserIdInAndPlanIdAndStatus(
                java.util.List.of(1L), 10L, SubscriptionStatus.ACTIVE
        )).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(new SubscribeUserCommand(10L, 1L, "user@test.com")))
                .isInstanceOf(ActiveSubscriptionAlreadyExistsException.class);

        verifyNoInteractions(paymentGateway);
    }

    @Test
    void handle_validCommand_generatesCheckout() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                10L, "Basic", "desc", BigDecimal.valueOf(9.99), 30, Set.of(), 0L);
        when(subscriptionPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(userSubscriptionRepository.existsByUserIdInAndPlanIdAndStatus(
                java.util.List.of(1L), 10L, SubscriptionStatus.ACTIVE
        )).thenReturn(false);

        handler.handle(new SubscribeUserCommand(10L, 1L, "user@test.com"));

        ArgumentCaptor<CheckoutCommand> captor = ArgumentCaptor.forClass(CheckoutCommand.class);
        verify(paymentGateway).generateCheckout(captor.capture());
        CheckoutCommand checkout = captor.getValue();

        assertThat(checkout.productName()).isEqualTo("Basic");
        assertThat(checkout.price()).isEqualByComparingTo("9.99");
        assertThat(checkout.userId()).isEqualTo(1L);
        assertThat(checkout.userEmail()).isEqualTo("user@test.com");
    }
}

