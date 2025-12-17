package dev.studentpp1.streamingservice.subscription.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.common.config.GlobalExceptionHandler;
import dev.studentpp1.streamingservice.payments.dto.PaymentResponse;
import dev.studentpp1.streamingservice.payments.entity.PaymentStatus;
import dev.studentpp1.streamingservice.subscription.dto.request.CreateFamilySubscriptionRequest;
import dev.studentpp1.streamingservice.subscription.dto.request.SubscribeRequest;
import dev.studentpp1.streamingservice.subscription.dto.response.UserSubscriptionDto;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.entity.UserSubscription;
import dev.studentpp1.streamingservice.subscription.exception.ActiveSubscriptionAlreadyExistsException;
import dev.studentpp1.streamingservice.subscription.exception.InvalidFamilyMemberException;
import dev.studentpp1.streamingservice.subscription.exception.SubscriptionAccessDeniedException;
import dev.studentpp1.streamingservice.subscription.exception.SubscriptionNotActiveException;
import dev.studentpp1.streamingservice.subscription.exception.SubscriptionNotFoundException;
import dev.studentpp1.streamingservice.subscription.mapper.UserSubscriptionMapper;
import dev.studentpp1.streamingservice.subscription.service.SubscriptionService;
import dev.studentpp1.streamingservice.users.entity.AppUser;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private UserSubscriptionMapper userSubscriptionMapper;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    private AppUser createTestUser(Long id, String email) {
        return AppUser.builder()
            .id(id)
            .email(email)
            .name("Test")
            .surname("User")
            .birthday(LocalDate.of(1990, 1, 1))
            .build();
    }

    private AuthenticatedUser createAuthenticatedUser(Long id, String email) {
        AppUser appUser = createTestUser(id, email);
        return new AuthenticatedUser(appUser);
    }

    private PaymentResponse createTestPaymentResponse() {
        return new PaymentResponse(
            PaymentStatus.PENDING.name(),
            "Payment session created",
            "cs_test_123",
            "https://stripe.com/checkout/cs_test_123"
        );
    }

    private SubscriptionPlan createTestPlan(BigDecimal price) {
        return SubscriptionPlan.builder()
            .id(1L)
            .name("PREMIUM")
            .price(price)
            .duration(30)
            .description("PREMIUM" + " plan")
            .build();
    }

    private UserSubscription createTestSubscription(Long id, AppUser user, SubscriptionPlan plan,
        SubscriptionStatus status) {
        return UserSubscription.builder()
            .id(id)
            .user(user)
            .plan(plan)
            .status(status)
            .startTime(LocalDateTime.of(2025, 1, 1, 12, 0))
            .endTime(LocalDateTime.of(2025, 1, 31, 12, 0))
            .build();
    }

    private UserSubscriptionDto createTestSubscriptionDto(Long id,
        SubscriptionStatus status) {
        return new UserSubscriptionDto(
            id,
            LocalDateTime.of(2025, 1, 1, 12, 0),
            LocalDateTime.of(2025, 1, 31, 12, 0),
            status,
            "PREMIUM");
    }

    // POST /api/subscriptions

    @Test
    void subscribe_withValidRequest_returnsCreatedAndPaymentResponse() throws Exception {
        SubscribeRequest request = new SubscribeRequest(1L);
        PaymentResponse paymentResponse = createTestPaymentResponse();
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");

        when(subscriptionService.subscribeUser(any(SubscribeRequest.class),
            any(AuthenticatedUser.class)))
            .thenReturn(paymentResponse);

        mockMvc.perform(post("/api/subscriptions")
                .with(user(authenticatedUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status", is(PaymentStatus.PENDING.name())))
            .andExpect(jsonPath("$.message", is("Payment session created")))
            .andExpect(jsonPath("$.sessionId", is("cs_test_123")))
            .andExpect(jsonPath("$.sessionUrl", is("https://stripe.com/checkout/cs_test_123")));

        verify(subscriptionService).subscribeUser(any(SubscribeRequest.class),
            any(AuthenticatedUser.class));
    }

    @Test
    void subscribe_withNullPlanId_returnsBadRequest() throws Exception {
        String requestJson = "{}";
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");

        mockMvc.perform(post("/api/subscriptions")
                .with(user(authenticatedUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));

        verify(subscriptionService, never()).subscribeUser(any(), any());
    }

    @Test
    void subscribe_withNegativePlanId_returnsBadRequest() throws Exception {
        SubscribeRequest request = new SubscribeRequest(-1L);
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");

        mockMvc.perform(post("/api/subscriptions")
                .with(user(authenticatedUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));

        verify(subscriptionService, never()).subscribeUser(any(), any());
    }

    @Test
    void subscribe_withZeroPlanId_returnsBadRequest() throws Exception {
        SubscribeRequest request = new SubscribeRequest(0L);
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");

        mockMvc.perform(post("/api/subscriptions")
                .with(user(authenticatedUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));

        verify(subscriptionService, never()).subscribeUser(any(), any());
    }

    // POST /api/subscriptions/family

    @Test
    void createFamilySubscription_withValidRequest_returnsCreatedAndPaymentResponse()
        throws Exception {
        List<String> memberEmails = List.of("member1@test.com", "member2@test.com");
        CreateFamilySubscriptionRequest request = new CreateFamilySubscriptionRequest(1L,
            memberEmails);
        PaymentResponse paymentResponse = createTestPaymentResponse();
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "main@test.com");

        when(
            subscriptionService.createFamilySubscription(any(CreateFamilySubscriptionRequest.class),
                any(AuthenticatedUser.class)))
            .thenReturn(paymentResponse);

        mockMvc.perform(post("/api/subscriptions/family")
                .with(user(authenticatedUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status", is(PaymentStatus.PENDING.name())))
            .andExpect(jsonPath("$.sessionId", is("cs_test_123")));

        verify(subscriptionService).createFamilySubscription(
            any(CreateFamilySubscriptionRequest.class),
            any(AuthenticatedUser.class));
    }

    @Test
    void createFamilySubscription_withNullPlanId_returnsBadRequest() throws Exception {
        String requestJson = "{\"memberEmails\": [\"member1@test.com\"]}";
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "main@test.com");

        mockMvc.perform(post("/api/subscriptions/family")
                .with(user(authenticatedUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));

        verify(subscriptionService, never()).createFamilySubscription(any(), any());
    }

    @Test
    void createFamilySubscription_withEmptyMemberEmails_returnsBadRequest() throws Exception {
        CreateFamilySubscriptionRequest request = new CreateFamilySubscriptionRequest(1L,
            List.of());
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "main@test.com");

        mockMvc.perform(post("/api/subscriptions/family")
                .with(user(authenticatedUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));

        verify(subscriptionService, never()).createFamilySubscription(any(), any());
    }

    @Test
    void createFamilySubscription_withTooManyMembers_returnsBadRequest() throws Exception {
        List<String> memberEmails = List.of(
            "member1@test.com",
            "member2@test.com",
            "member3@test.com",
            "member4@test.com",
            "member5@test.com"
        );
        CreateFamilySubscriptionRequest request = new CreateFamilySubscriptionRequest(1L,
            memberEmails);
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "main@test.com");

        mockMvc.perform(post("/api/subscriptions/family")
                .with(user(authenticatedUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));

        verify(subscriptionService, never()).createFamilySubscription(any(), any());
    }

    @Test
    void createFamilySubscription_withInvalidEmailFormat_returnsBadRequest() throws Exception {
        List<String> memberEmails = List.of("invalid-email");
        CreateFamilySubscriptionRequest request = new CreateFamilySubscriptionRequest(1L,
            memberEmails);
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "main@test.com");

        mockMvc.perform(post("/api/subscriptions/family")
                .with(user(authenticatedUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));

        verify(subscriptionService, never()).createFamilySubscription(any(), any());
    }

    @Test
    void createFamilySubscription_whenMainUserInMemberList_returnsBadRequest() throws Exception {
        List<String> memberEmails = List.of("member1@test.com");
        CreateFamilySubscriptionRequest request = new CreateFamilySubscriptionRequest(1L,
            memberEmails);
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "main@test.com");

        when(
            subscriptionService.createFamilySubscription(any(CreateFamilySubscriptionRequest.class),
                any(AuthenticatedUser.class)))
            .thenThrow(new InvalidFamilyMemberException());

        mockMvc.perform(post("/api/subscriptions/family")
                .with(user(authenticatedUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.message", is("Cannot add yourself as a family member")));

        verify(subscriptionService).createFamilySubscription(
            any(CreateFamilySubscriptionRequest.class),
            any(AuthenticatedUser.class));
    }

    @Test
    void createFamilySubscription_whenMemberHasActiveSubscription_returnsConflict()
        throws Exception {
        List<String> memberEmails = List.of("member1@test.com");
        CreateFamilySubscriptionRequest request = new CreateFamilySubscriptionRequest(1L,
            memberEmails);
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "main@test.com");

        when(
            subscriptionService.createFamilySubscription(any(CreateFamilySubscriptionRequest.class),
                any(AuthenticatedUser.class)))
            .thenThrow(
                new ActiveSubscriptionAlreadyExistsException("member1@test.com",
                    "PREMIUM"));

        mockMvc.perform(post("/api/subscriptions/family")
                .with(user(authenticatedUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status", is(409)));

        verify(subscriptionService).createFamilySubscription(
            any(CreateFamilySubscriptionRequest.class),
            any(AuthenticatedUser.class));
    }

    // GET /api/subscriptions

    @Test
    void getMySubscriptions_withValidAuth_returnsPageOfSubscriptions() throws Exception {
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");
        AppUser user = authenticatedUser.getAppUser();
        SubscriptionPlan plan = createTestPlan(new BigDecimal("20.00"));

        UserSubscription subscription1 = createTestSubscription(1L, user, plan,
            SubscriptionStatus.ACTIVE);
        UserSubscription subscription2 = createTestSubscription(2L, user, plan,
            SubscriptionStatus.CANCELLED);

        UserSubscriptionDto dto1 = createTestSubscriptionDto(1L,
            SubscriptionStatus.ACTIVE);
        UserSubscriptionDto dto2 = createTestSubscriptionDto(2L,
            SubscriptionStatus.CANCELLED);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "endTime"));
        Page<UserSubscription> subscriptionPage = new PageImpl<>(
            List.of(subscription1, subscription2),
            pageable,
            2);

        when(subscriptionService.getUserSubscriptions(any(AuthenticatedUser.class),
            any(Pageable.class)))
            .thenReturn(subscriptionPage);
        when(userSubscriptionMapper.toDto(subscription1)).thenReturn(dto1);
        when(userSubscriptionMapper.toDto(subscription2)).thenReturn(dto2);

        mockMvc.perform(get("/api/subscriptions")
                .with(user(authenticatedUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].id", is(1)))
            .andExpect(jsonPath("$.content[0].planName", is("PREMIUM")))
            .andExpect(jsonPath("$.content[0].status", is("ACTIVE")))
            .andExpect(jsonPath("$.content[1].id", is(2)))
            .andExpect(jsonPath("$.content[1].status", is("CANCELLED")))
            .andExpect(jsonPath("$.totalElements", is(2)));

        verify(subscriptionService).getUserSubscriptions(any(AuthenticatedUser.class),
            any(Pageable.class));
    }

    @Test
    void getMySubscriptions_withPaginationParams_respectsPaginationAndSorting() throws Exception {
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");
        AppUser user = authenticatedUser.getAppUser();
        SubscriptionPlan plan = createTestPlan(new BigDecimal("20.00"));

        UserSubscription subscription = createTestSubscription(1L, user, plan,
            SubscriptionStatus.ACTIVE);
        UserSubscriptionDto dto = createTestSubscriptionDto(1L,
            SubscriptionStatus.ACTIVE);

        Pageable pageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "endTime"));
        Page<UserSubscription> subscriptionPage = new PageImpl<>(
            List.of(subscription),
            pageable,
            6);

        when(subscriptionService.getUserSubscriptions(any(AuthenticatedUser.class),
            any(Pageable.class)))
            .thenReturn(subscriptionPage);
        when(userSubscriptionMapper.toDto(subscription)).thenReturn(dto);

        mockMvc.perform(get("/api/subscriptions")
                .with(user(authenticatedUser))
                .param("page", "1")
                .param("size", "5")
                .param("sort", "endTime,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.totalElements", is(6)))
            .andExpect(jsonPath("$.number", is(1)))
            .andExpect(jsonPath("$.size", is(5)));

        verify(subscriptionService).getUserSubscriptions(any(AuthenticatedUser.class),
            any(Pageable.class));
    }

    @Test
    void getMySubscriptions_whenNoSubscriptions_returnsEmptyPage() throws Exception {
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "endTime"));
        Page<UserSubscription> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(subscriptionService.getUserSubscriptions(any(AuthenticatedUser.class),
            any(Pageable.class)))
            .thenReturn(emptyPage);

        mockMvc.perform(get("/api/subscriptions")
                .with(user(authenticatedUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(0)))
            .andExpect(jsonPath("$.totalElements", is(0)));

        verify(subscriptionService).getUserSubscriptions(any(AuthenticatedUser.class),
            any(Pageable.class));
    }

    // DELETE /api/subscriptions/{id}

    @Test
    void cancelSubscription_withValidId_returnsNoContent() throws Exception {
        Long subscriptionId = 1L;
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");

        mockMvc.perform(delete("/api/subscriptions/{id}", subscriptionId)
                .with(user(authenticatedUser)))
            .andExpect(status().isNoContent());

        verify(subscriptionService).cancelSubscription(eq(subscriptionId),
            any(AuthenticatedUser.class));
    }

    @Test
    void cancelSubscription_whenUserDoesNotOwnSubscription_returnsForbidden() throws Exception {
        Long subscriptionId = 1L;
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");

        doThrow(new SubscriptionAccessDeniedException())
            .when(subscriptionService).cancelSubscription(eq(subscriptionId),
                any(AuthenticatedUser.class));

        mockMvc.perform(delete("/api/subscriptions/{id}", subscriptionId)
                .with(user(authenticatedUser)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status", is(403)))
            .andExpect(
                jsonPath("$.message", is(
                    "You are not authorized to cancel this subscription")));

        verify(subscriptionService).cancelSubscription(eq(subscriptionId),
            any(AuthenticatedUser.class));
    }

    @Test
    void cancelSubscription_whenSubscriptionNotActive_returnsBadRequest() throws Exception {
        Long subscriptionId = 1L;
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");

        doThrow(new SubscriptionNotActiveException())
            .when(subscriptionService).cancelSubscription(eq(subscriptionId),
                any(AuthenticatedUser.class));

        mockMvc.perform(delete("/api/subscriptions/{id}", subscriptionId)
                .with(user(authenticatedUser)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.message", is("Only active subscriptions can be cancelled")));

        verify(subscriptionService).cancelSubscription(eq(subscriptionId),
            any(AuthenticatedUser.class));
    }

    @Test
    void cancelSubscription_whenSubscriptionNotFound_returnsNotFound() throws Exception {
        Long subscriptionId = 999L;
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");

        doThrow(new SubscriptionNotFoundException(subscriptionId))
            .when(subscriptionService).cancelSubscription(eq(subscriptionId),
                any(AuthenticatedUser.class));

        mockMvc.perform(delete("/api/subscriptions/{id}", subscriptionId)
                .with(user(authenticatedUser)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)));

        verify(subscriptionService).cancelSubscription(eq(subscriptionId),
            any(AuthenticatedUser.class));
    }

}
