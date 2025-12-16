package dev.studentpp1.streamingservice.subscription.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.common.time.ClockService;
import dev.studentpp1.streamingservice.payments.dto.PaymentResponse;
import dev.studentpp1.streamingservice.payments.entity.PaymentStatus;
import dev.studentpp1.streamingservice.payments.service.PaymentService;
import dev.studentpp1.streamingservice.subscription.dto.request.CreateFamilySubscriptionRequest;
import dev.studentpp1.streamingservice.subscription.dto.request.SubscribeRequest;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.entity.UserSubscription;
import dev.studentpp1.streamingservice.subscription.exception.ActiveSubscriptionAlreadyExistsException;
import dev.studentpp1.streamingservice.subscription.exception.InvalidFamilyMemberException;
import dev.studentpp1.streamingservice.subscription.exception.SerializationException;
import dev.studentpp1.streamingservice.subscription.exception.SubscriptionAccessDeniedException;
import dev.studentpp1.streamingservice.subscription.exception.SubscriptionNotActiveException;
import dev.studentpp1.streamingservice.subscription.repository.UserSubscriptionRepository;
import dev.studentpp1.streamingservice.subscription.service.utils.SubscriptionPlanUtils;
import dev.studentpp1.streamingservice.subscription.service.utils.UserSubscriptionUtils;
import dev.studentpp1.streamingservice.users.entity.AppUser;
import dev.studentpp1.streamingservice.users.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionPlanUtils subscriptionPlanUtils;

    @Mock
    private UserSubscriptionRepository userSubscriptionRepository;

    @Mock
    private UserService userService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private UserSubscriptionUtils userSubscriptionUtils;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ClockService clockService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private AuthenticatedUser mockAuthenticatedUser(Long userId, String email) {
        AppUser appUser = AppUser.builder()
            .id(userId)
            .email(email)
            .name("Test")
            .surname("User")
            .birthday(LocalDate.of(1990, 1, 1))
            .build();
        return new AuthenticatedUser(appUser);
    }

    private SubscriptionPlan createTestPlan(Long id, String name, BigDecimal price, int duration) {
        return SubscriptionPlan.builder()
            .id(id)
            .name(name)
            .price(price)
            .duration(duration)
            .description(name + " plan")
            .build();
    }

    private SubscriptionPlan createTestPlan() {
        return createTestPlan(1L, "PREMIUM", new BigDecimal("20.00"), 30);
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

    private PaymentResponse createTestPaymentResponse() {
        return new PaymentResponse(
            PaymentStatus.PENDING.name(),
            "Payment session created",
            "cs_123",
            "https://stripe.com/checkout/cs_123"
        );
    }

    @Test
    void subscribeUser_callsPaymentServiceCheckout_returnsResponse() {
        Long planId = 1L;
        SubscribeRequest request = new SubscribeRequest(planId);
        AuthenticatedUser currentUser = mockAuthenticatedUser(42L, "user@test.com");

        SubscriptionPlan plan = createTestPlan(planId, "PREMIUM", new BigDecimal("20.00"), 30);
        PaymentResponse expectedResponse = createTestPaymentResponse();

        when(subscriptionPlanUtils.findById(planId)).thenReturn(plan);
        when(paymentService.checkoutProduct(plan)).thenReturn(expectedResponse);

        PaymentResponse response = subscriptionService.subscribeUser(request, currentUser);

        assertThat(response).isEqualTo(expectedResponse);
        assertThat(response.sessionId()).isEqualTo("cs_123");
        assertThat(response.status()).isEqualTo(PaymentStatus.PENDING.name());

        verify(subscriptionPlanUtils).findById(planId);
        verify(paymentService).checkoutProduct(plan);
    }

    @Test
    void createUserSubscription_createsAndSavesSubscription_returnsSubscription() {
        String planName = "PREMIUM";
        String userId = "42";
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 0);

        SubscriptionPlan plan = createTestPlan(1L, planName, new BigDecimal("20.00"), 30);
        AppUser user = createTestUser(42L, "user@test.com");

        when(subscriptionPlanUtils.findByName(planName)).thenReturn(plan);
        when(userService.findById(42L)).thenReturn(user);
        when(clockService.now()).thenReturn(now);
        when(userSubscriptionRepository.save(any(UserSubscription.class))).thenAnswer(inv -> {
            UserSubscription subscription = inv.getArgument(0);
            subscription.setId(1L);
            return subscription;
        });

        UserSubscription result = subscriptionService.createUserSubscription(planName, userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getPlan()).isEqualTo(plan);
        assertThat(result.getStartTime()).isEqualTo(now);
        assertThat(result.getEndTime()).isEqualTo(now.plusDays(30));
        assertThat(result.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);

        verify(subscriptionPlanUtils).findByName(planName);
        verify(userService).findById(42L);
        verify(clockService).now();
        verify(userSubscriptionRepository).save(any(UserSubscription.class));
    }

    @Test
    void createUserSubscription_setsCorrectDatesAndStatus() {
        String planName = "BASIC";
        String userId = "10";
        LocalDateTime fixedTime = LocalDateTime.of(2025, 6, 15, 10, 30);

        SubscriptionPlan plan = createTestPlan(2L, planName, new BigDecimal("10.00"), 7);
        AppUser user = createTestUser(10L, "basic@test.com");

        when(subscriptionPlanUtils.findByName(planName)).thenReturn(plan);
        when(userService.findById(10L)).thenReturn(user);
        when(clockService.now()).thenReturn(fixedTime);
        when(userSubscriptionRepository.save(any(UserSubscription.class))).thenAnswer(
            inv -> inv.getArgument(0));

        subscriptionService.createUserSubscription(planName, userId);

        ArgumentCaptor<UserSubscription> captor = ArgumentCaptor.forClass(UserSubscription.class);
        verify(userSubscriptionRepository).save(captor.capture());

        UserSubscription saved = captor.getValue();
        assertThat(saved.getStartTime()).isEqualTo(fixedTime);
        assertThat(saved.getEndTime()).isEqualTo(fixedTime.plusDays(7));
        assertThat(saved.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void createFamilySubscription_createsCheckoutSession_withMemberEmails()
        throws JsonProcessingException {
        Long planId = 1L;
        List<String> memberEmails = List.of("member1@test.com", "member2@test.com");
        CreateFamilySubscriptionRequest request = new CreateFamilySubscriptionRequest(planId,
            memberEmails);
        AuthenticatedUser currentUser = mockAuthenticatedUser(42L, "main@test.com");

        SubscriptionPlan plan = createTestPlan(planId, "FAMILY", new BigDecimal("30.00"), 30);
        AppUser member1 = createTestUser(10L, "member1@test.com");
        AppUser member2 = createTestUser(20L, "member2@test.com");

        String memberEmailsJson = "[\"member1@test.com\",\"member2@test.com\"]";
        PaymentResponse expectedResponse = createTestPaymentResponse();

        when(subscriptionPlanUtils.findById(planId)).thenReturn(plan);
        when(userService.findByEmail("member1@test.com")).thenReturn(member1);
        when(userService.findByEmail("member2@test.com")).thenReturn(member2);
        when(userSubscriptionRepository.findByUser(any())).thenReturn(List.of());
        when(objectMapper.writeValueAsString(memberEmails)).thenReturn(memberEmailsJson);
        when(paymentService.checkoutProduct(eq(plan), any())).thenReturn(expectedResponse);

        PaymentResponse response = subscriptionService.createFamilySubscription(request,
            currentUser);

        assertThat(response).isEqualTo(expectedResponse);

        verify(subscriptionPlanUtils).findById(planId);
        verify(userService).findByEmail("member1@test.com");
        verify(userService).findByEmail("member2@test.com");
        verify(objectMapper).writeValueAsString(memberEmails);

        ArgumentCaptor<Map<String, String>> metadataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(paymentService).checkoutProduct(eq(plan), metadataCaptor.capture());

        Map<String, String> metadata = metadataCaptor.getValue();
        assertThat(metadata).containsEntry(PaymentService.FAMILY_MEMBER_EMAILS, memberEmailsJson);
    }

    @Test
    void createFamilySubscription_whenMainUserInMemberList_throwsInvalidFamilyMemberException() {
        Long planId = 1L;
        List<String> memberEmails = List.of("main@test.com", "member1@test.com");
        CreateFamilySubscriptionRequest request = new CreateFamilySubscriptionRequest(planId,
            memberEmails);
        AuthenticatedUser currentUser = mockAuthenticatedUser(42L, "main@test.com");

        SubscriptionPlan plan = createTestPlan(planId, "FAMILY", new BigDecimal("30.00"), 30);
        AppUser mainUser = currentUser.getAppUser();

        when(subscriptionPlanUtils.findById(planId)).thenReturn(plan);
        when(userService.findByEmail("main@test.com")).thenReturn(mainUser);

        assertThatThrownBy(() -> subscriptionService.createFamilySubscription(request, currentUser))
            .isInstanceOf(InvalidFamilyMemberException.class)
            .hasMessage("Cannot add yourself as a family member");

        verify(subscriptionPlanUtils).findById(planId);
        verify(userService).findByEmail("main@test.com");
        verifyNoInteractions(objectMapper, paymentService);
    }

    @Test
    void createFamilySubscription_whenMemberHasActiveSubscription_throwsActiveSubscriptionException() {
        Long planId = 1L;
        List<String> memberEmails = List.of("member1@test.com");
        CreateFamilySubscriptionRequest request = new CreateFamilySubscriptionRequest(planId,
            memberEmails);
        AuthenticatedUser currentUser = mockAuthenticatedUser(42L, "main@test.com");

        SubscriptionPlan plan = createTestPlan(planId, "FAMILY", new BigDecimal("30.00"), 30);
        AppUser member1 = createTestUser(10L, "member1@test.com");

        UserSubscription existingSubscription = createTestSubscription(1L, member1, plan,
            SubscriptionStatus.ACTIVE);

        when(subscriptionPlanUtils.findById(planId)).thenReturn(plan);
        when(userService.findByEmail("member1@test.com")).thenReturn(member1);
        when(userSubscriptionRepository.findByUser(member1)).thenReturn(
            List.of(existingSubscription));

        assertThatThrownBy(() -> subscriptionService.createFamilySubscription(request, currentUser))
            .isInstanceOf(ActiveSubscriptionAlreadyExistsException.class)
            .hasMessageContaining("member1@test.com")
            .hasMessageContaining("FAMILY");

        verify(subscriptionPlanUtils).findById(planId);
        verify(userService).findByEmail("member1@test.com");
        verify(userSubscriptionRepository).findByUser(member1);
        verifyNoInteractions(objectMapper, paymentService);
    }

    @Test
    void createFamilySubscription_whenSerializationFails_throwsSerializationException()
        throws JsonProcessingException {
        Long planId = 1L;
        List<String> memberEmails = List.of("member1@test.com");
        CreateFamilySubscriptionRequest request = new CreateFamilySubscriptionRequest(planId,
            memberEmails);
        AuthenticatedUser currentUser = mockAuthenticatedUser(42L, "main@test.com");

        SubscriptionPlan plan = createTestPlan(planId, "FAMILY", new BigDecimal("30.00"), 30);
        AppUser member1 = createTestUser(10L, "member1@test.com");

        when(subscriptionPlanUtils.findById(planId)).thenReturn(plan);
        when(userService.findByEmail("member1@test.com")).thenReturn(member1);
        when(userSubscriptionRepository.findByUser(any())).thenReturn(List.of());
        when(objectMapper.writeValueAsString(memberEmails))
            .thenThrow(new JsonProcessingException("Serialization error") {
            });

        assertThatThrownBy(() -> subscriptionService.createFamilySubscription(request, currentUser))
            .isInstanceOf(SerializationException.class)
            .hasMessage(SubscriptionService.ERROR_SERIALIZING_FAMILY_MEMBER_EMAILS)
            .hasCauseInstanceOf(JsonProcessingException.class);

        verify(objectMapper).writeValueAsString(memberEmails);
        verifyNoInteractions(paymentService);
    }

    @Test
    void createFamilySubscriptionAfterPayment_createsMultipleSubscriptions_forAllUsers() {
        String userId = "42";
        String planName = "FAMILY";
        List<String> memberEmails = List.of("member1@test.com", "member2@test.com");
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 0);

        SubscriptionPlan plan = createTestPlan(1L, planName, new BigDecimal("30.00"), 30);
        AppUser mainUser = createTestUser(42L, "main@test.com");
        AppUser member1 = createTestUser(10L, "member1@test.com");
        AppUser member2 = createTestUser(20L, "member2@test.com");

        when(subscriptionPlanUtils.findByName(planName)).thenReturn(plan);
        when(userService.findById(42L)).thenReturn(mainUser);
        when(userService.findByEmail("member1@test.com")).thenReturn(member1);
        when(userService.findByEmail("member2@test.com")).thenReturn(member2);
        when(clockService.now()).thenReturn(now);
        when(userSubscriptionRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<UserSubscription> result = subscriptionService.createFamilySubscriptionAfterPayment(
            userId, planName, memberEmails);

        assertThat(result).hasSize(3);
        assertThat(result)
            .extracting(UserSubscription::getUser)
            .containsExactlyInAnyOrder(member1, member2, mainUser);
        assertThat(result)
            .allMatch(sub -> sub.getPlan().equals(plan))
            .allMatch(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE)
            .allMatch(sub -> sub.getStartTime().equals(now))
            .allMatch(sub -> sub.getEndTime().equals(now.plusDays(30)));

        verify(subscriptionPlanUtils).findByName(planName);
        verify(userService).findById(42L);
        verify(userService).findByEmail("member1@test.com");
        verify(userService).findByEmail("member2@test.com");
        verify(clockService).now();
        verify(userSubscriptionRepository).saveAll(anyList());
    }

    @Test
    void createFamilySubscriptionAfterPayment_allSubscriptionsHaveSameTimestamps() {
        String userId = "42";
        String planName = "FAMILY";
        List<String> memberEmails = List.of("member1@test.com");
        LocalDateTime fixedTime = LocalDateTime.of(2025, 3, 10, 15, 45);

        SubscriptionPlan plan = createTestPlan(1L, planName, new BigDecimal("30.00"), 30);
        AppUser mainUser = createTestUser(42L, "main@test.com");
        AppUser member1 = createTestUser(10L, "member1@test.com");

        when(subscriptionPlanUtils.findByName(planName)).thenReturn(plan);
        when(userService.findById(42L)).thenReturn(mainUser);
        when(userService.findByEmail("member1@test.com")).thenReturn(member1);
        when(clockService.now()).thenReturn(fixedTime);
        when(userSubscriptionRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        subscriptionService.createFamilySubscriptionAfterPayment(userId, planName, memberEmails);

        ArgumentCaptor<List<UserSubscription>> captor = ArgumentCaptor.forClass(List.class);
        verify(userSubscriptionRepository).saveAll(captor.capture());

        List<UserSubscription> savedSubscriptions = captor.getValue();
        assertThat(savedSubscriptions).hasSize(2);
        assertThat(savedSubscriptions)
            .allMatch(sub -> sub.getStartTime().equals(fixedTime))
            .allMatch(sub -> sub.getEndTime().equals(fixedTime.plusDays(30)));
    }

    @Test
    void getUserSubscriptions_returnsPagedSubscriptions_forAuthenticatedUser() {
        Long userId = 42L;
        AuthenticatedUser currentUser = mockAuthenticatedUser(userId, "user@test.com");
        Pageable pageable = PageRequest.of(0, 10);

        AppUser user = currentUser.getAppUser();
        SubscriptionPlan plan = createTestPlan();

        UserSubscription subscription1 = createTestSubscription(1L, user, plan,
            SubscriptionStatus.ACTIVE);
        UserSubscription subscription2 = createTestSubscription(2L, user, plan,
            SubscriptionStatus.CANCELLED);

        Page<UserSubscription> expectedPage = new PageImpl<>(
            List.of(subscription1, subscription2),
            pageable,
            2
        );

        when(userService.findById(userId)).thenReturn(user);
        when(userSubscriptionRepository.findAllByUser(user, pageable)).thenReturn(expectedPage);

        Page<UserSubscription> result = subscriptionService.getUserSubscriptions(currentUser,
            pageable);

        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(subscription1, subscription2);

        verify(userService).findById(userId);
        verify(userSubscriptionRepository).findAllByUser(user, pageable);
    }

    @Test
    void cancelSubscription_updatesStatusToCancelled_whenUserOwnsIt() {
        Long subscriptionId = 1L;
        Long userId = 42L;
        AuthenticatedUser currentUser = mockAuthenticatedUser(userId, "user@test.com");

        AppUser user = currentUser.getAppUser();
        SubscriptionPlan plan = createTestPlan();

        UserSubscription subscription = createTestSubscription(subscriptionId, user, plan,
            SubscriptionStatus.ACTIVE);

        when(userSubscriptionUtils.findByIdWithLock(subscriptionId)).thenReturn(subscription);
        when(userSubscriptionRepository.save(any(UserSubscription.class))).thenAnswer(
            inv -> inv.getArgument(0));

        subscriptionService.cancelSubscription(subscriptionId, currentUser);

        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);

        verify(userSubscriptionUtils).findByIdWithLock(subscriptionId);
        verify(userSubscriptionRepository).save(subscription);
    }

    @Test
    void cancelSubscription_whenUserDoesNotOwnSubscription_throwsAccessDeniedException() {
        Long subscriptionId = 1L;
        Long userId = 42L;
        AuthenticatedUser currentUser = mockAuthenticatedUser(userId, "user@test.com");

        AppUser otherUser = createTestUser(99L, "other@test.com");
        SubscriptionPlan plan = createTestPlan();

        UserSubscription subscription = createTestSubscription(subscriptionId, otherUser, plan,
            SubscriptionStatus.ACTIVE);

        when(userSubscriptionUtils.findByIdWithLock(subscriptionId)).thenReturn(subscription);

        assertThatThrownBy(
            () -> subscriptionService.cancelSubscription(subscriptionId, currentUser))
            .isInstanceOf(SubscriptionAccessDeniedException.class)
            .hasMessage("You are not authorized to cancel this subscription");

        verify(userSubscriptionUtils).findByIdWithLock(subscriptionId);
        verify(userSubscriptionRepository, never()).save(any());
    }

    @Test
    void cancelSubscription_whenSubscriptionNotActive_throwsSubscriptionNotActiveException() {
        Long subscriptionId = 1L;
        Long userId = 42L;
        AuthenticatedUser currentUser = mockAuthenticatedUser(userId, "user@test.com");

        AppUser user = currentUser.getAppUser();
        SubscriptionPlan plan = createTestPlan();

        UserSubscription subscription = createTestSubscription(subscriptionId, user, plan,
            SubscriptionStatus.CANCELLED);

        when(userSubscriptionUtils.findByIdWithLock(subscriptionId)).thenReturn(subscription);

        assertThatThrownBy(
            () -> subscriptionService.cancelSubscription(subscriptionId, currentUser))
            .isInstanceOf(SubscriptionNotActiveException.class)
            .hasMessage("Only active subscriptions can be cancelled");

        verify(userSubscriptionUtils).findByIdWithLock(subscriptionId);
        verify(userSubscriptionRepository, never()).save(any());
    }
}

