package dev.studentpp1.streamingservice.subscription.application.command.subscription;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.studentpp1.streamingservice.subscription.domain.exception.ActiveSubscriptionAlreadyExistsException;
import dev.studentpp1.streamingservice.subscription.domain.exception.InvalidFamilyMemberException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SerializationException;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutCommand;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriberContext;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriberProvider;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionPaymentGateway;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CreateFamilySubscriptionHandlerTest {

    private SubscriptionPlanRepository subscriptionPlanRepository;
    private UserSubscriptionRepository userSubscriptionRepository;
    private SubscriberProvider subscriberProvider;
    private SubscriptionPaymentGateway paymentGateway;
    private ObjectMapper objectMapper;
    private CreateFamilySubscriptionHandler handler;

    @BeforeEach
    void setUp() {
        subscriptionPlanRepository = mock(SubscriptionPlanRepository.class);
        userSubscriptionRepository = mock(UserSubscriptionRepository.class);
        subscriberProvider = mock(SubscriberProvider.class);
        paymentGateway = mock(SubscriptionPaymentGateway.class);
        objectMapper = mock(ObjectMapper.class);
        handler = new CreateFamilySubscriptionHandler(
                subscriptionPlanRepository,
                userSubscriptionRepository,
                subscriberProvider,
                paymentGateway,
                objectMapper
        );
    }

    @Test
    void handle_mainUserInsideFamily_throwsInvalidFamilyMemberException() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                10L, "Family", "desc", BigDecimal.valueOf(19.99), 30, Set.of(), 0L);
        when(subscriptionPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(subscriberProvider.getById(1L)).thenReturn(new SubscriberContext(1L, "main@mail.com"));
        when(subscriberProvider.getByEmail("main@mail.com")).thenReturn(new SubscriberContext(1L, "main@mail.com"));

        assertThatThrownBy(() -> handler.handle(new CreateFamilySubscriptionCommand(
                10L, List.of("main@mail.com"), 1L
        ))).isInstanceOf(InvalidFamilyMemberException.class);

        verifyNoInteractions(paymentGateway);
    }

    @Test
    void handle_activeSubscriptionExists_throwsActiveSubscriptionAlreadyExistsException() {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                10L, "Family", "desc", BigDecimal.valueOf(19.99), 30, Set.of(), 0L);
        SubscriberContext mainUser = new SubscriberContext(1L, "main@mail.com");
        SubscriberContext member = new SubscriberContext(2L, "member@mail.com");

        when(subscriptionPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(subscriberProvider.getById(1L)).thenReturn(mainUser);
        when(subscriberProvider.getByEmail("member@mail.com")).thenReturn(member);
        when(userSubscriptionRepository.existsByUserIdInAndPlanIdAndStatus(
                List.of(2L, 1L), 10L, SubscriptionStatus.ACTIVE
        )).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(new CreateFamilySubscriptionCommand(
                10L, List.of("member@mail.com"), 1L
        ))).isInstanceOf(ActiveSubscriptionAlreadyExistsException.class);

        verifyNoInteractions(paymentGateway);
    }

    @Test
    void handle_validCommand_generatesCheckoutWithMetadata() throws Exception {
        SubscriptionPlan plan = SubscriptionPlan.restore(
                10L, "Family", "desc", BigDecimal.valueOf(19.99), 30, Set.of(), 0L);
        SubscriberContext mainUser = new SubscriberContext(1L, "main@mail.com");
        SubscriberContext member = new SubscriberContext(2L, "member@mail.com");

        when(subscriptionPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(subscriberProvider.getById(1L)).thenReturn(mainUser);
        when(subscriberProvider.getByEmail("member@mail.com")).thenReturn(member);
        when(userSubscriptionRepository.existsByUserIdInAndPlanIdAndStatus(
                List.of(2L, 1L), 10L, SubscriptionStatus.ACTIVE
        )).thenReturn(false);
        when(objectMapper.writeValueAsString(List.of("member@mail.com")))
                .thenReturn("[\"member@mail.com\"]");

        handler.handle(new CreateFamilySubscriptionCommand(10L, List.of("member@mail.com"), 1L));

        ArgumentCaptor<CheckoutCommand> captor = ArgumentCaptor.forClass(CheckoutCommand.class);
        verify(paymentGateway).generateCheckout(captor.capture());
        CheckoutCommand checkout = captor.getValue();

        assertThat(checkout.productName()).isEqualTo("Family");
        assertThat(checkout.price()).isEqualByComparingTo("19.99");
        assertThat(checkout.userId()).isEqualTo(1L);
        assertThat(checkout.metadata())
                .containsEntry(CreateFamilySubscriptionHandler.FAMILY_MEMBER_EMAILS_KEY, "[\"member@mail.com\"]");
    }
}

