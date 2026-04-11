package dev.studentpp1.streamingservice.subscription.application.cqs;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionPlanService;
import dev.studentpp1.streamingservice.subscription.application.usecase.SubscriptionService;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.presentation.dto.request.CreateSubscriptionPlanRequest;
import dev.studentpp1.streamingservice.subscription.presentation.dto.request.SubscribeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class SubscriptionCommandHandlerUnitTest {
    @Mock
    private SubscriptionPlanService subscriptionPlanService;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private SubscriptionPlan subscriptionPlan;
    private SubscriptionCommandHandler handler;
    @BeforeEach
    void setUp() {
        handler = new SubscriptionCommandHandler(subscriptionPlanService, subscriptionService);
    }
    @Test
    void createPlan_returnsCreatedPlanId() {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", "Basic plan", BigDecimal.valueOf(9.99), 30, List.of(1L, 2L));
        when(subscriptionPlanService.createPlan(request))
                .thenReturn(new SubscriptionPlanService.PlanWithMovies(subscriptionPlan, List.of()));
        when(subscriptionPlan.getId()).thenReturn(7L);
        Long result = handler.handle(new SubscriptionCqs.CreatePlanCommand(request));
        assertThat(result).isEqualTo(7L);
        verify(subscriptionPlanService).createPlan(request);
    }
    @Test
    void subscribeUser_returnsCheckoutResult() {
        SubscribeRequest request = new SubscribeRequest(10L);
        CheckoutResult checkoutResult = new CheckoutResult("PENDING", "ok", "sess_1", "https://example.com");
        when(subscriptionService.subscribeUser(request, 5L)).thenReturn(checkoutResult);
        CheckoutResult result = handler.handle(new SubscriptionCqs.SubscribeCommand(request, 5L));
        assertThat(result).isSameAs(checkoutResult);
        verify(subscriptionService).subscribeUser(request, 5L);
    }
    @Test
    void cancelSubscription_delegatesToService() {
        handler.handle(new SubscriptionCqs.CancelSubscriptionCommand(9L, 5L));
        verify(subscriptionService).cancelSubscription(9L, 5L);
    }
}


