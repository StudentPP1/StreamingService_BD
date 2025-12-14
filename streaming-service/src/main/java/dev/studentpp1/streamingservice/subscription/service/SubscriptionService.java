package dev.studentpp1.streamingservice.subscription.service;

import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.payments.dto.PaymentResponse;
import dev.studentpp1.streamingservice.payments.service.PaymentService;
import dev.studentpp1.streamingservice.subscription.dto.CreateFamilySubscriptionRequest;
import dev.studentpp1.streamingservice.subscription.dto.SubscribeRequest;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.entity.UserSubscription;
import dev.studentpp1.streamingservice.subscription.exception.SubscriptionNotFoundException;
import dev.studentpp1.streamingservice.subscription.repository.UserSubscriptionRepository;
import dev.studentpp1.streamingservice.users.entity.AppUser;
import dev.studentpp1.streamingservice.users.service.UserService;
import java.util.ArrayList;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionPlanUtils subscriptionPlanUtils;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserService userService;
    private final PaymentService paymentService;
    private final UserSubscriptionUtils userSubscriptionUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public PaymentResponse subscribeUser(SubscribeRequest request, AuthenticatedUser currentUser) {
        SubscriptionPlan plan = subscriptionPlanUtils.findById(request.planId());

        return paymentService.checkoutProduct(plan);
    }

    public UserSubscription createUserSubscription(String planName, String userId) {
        SubscriptionPlan plan = subscriptionPlanUtils.findByName(planName);
        AppUser user = userService.findById(Long.parseLong(userId));
        LocalDateTime startTime = LocalDateTime.now();
        UserSubscription subscription = UserSubscription.builder()
            .user(user)
            .plan(plan)
            .startTime(startTime)
            .endTime(startTime.plusDays(plan.getDuration()))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        return userSubscriptionRepository.save(subscription);
    }

    @Transactional
    public PaymentResponse createFamilySubscription(CreateFamilySubscriptionRequest request, AuthenticatedUser currentUser) {
        SubscriptionPlan plan = subscriptionPlanUtils.findById(request.planId());
        AppUser mainUser = currentUser.getAppUser();

        List<AppUser> familyMembers = findFamilyMembers(request.memberEmails(), mainUser);
        List<AppUser> allUsers = buildAllUsersList(familyMembers, mainUser);

        validateNoActiveSubscriptions(allUsers, plan);

        String memberEmailsJson;
        try {
            memberEmailsJson = objectMapper.writeValueAsString(request.memberEmails());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize family member emails", e);
        }

        Map<String, String> metadata = Map.of(PaymentService.FAMILY_MEMBER_EMAILS, memberEmailsJson);
        return paymentService.checkoutProduct(plan, metadata);
    }

    @Transactional
    public List<UserSubscription> createFamilySubscriptionAfterPayment(String userId, String planName, List<String> memberEmails) {
        SubscriptionPlan plan = subscriptionPlanUtils.findByName(planName);
        AppUser mainUser = userService.findById(Long.parseLong(userId));

        List<AppUser> familyMembers = findFamilyMembers(memberEmails, mainUser);
        List<AppUser> allUsers = buildAllUsersList(familyMembers, mainUser);

        return createAndSaveSubscriptions(allUsers, plan);
    }

    private List<AppUser> findFamilyMembers(List<String> memberEmails, AppUser mainUser) {
        List<AppUser> familyMembers = new ArrayList<>();

        for (String email : memberEmails) {
            AppUser member = userService.findByEmail(email);

            if (member.getId().equals(mainUser.getId())) {
                throw new IllegalArgumentException("Cannot add yourself as a family member");
            }
            familyMembers.add(member);
        }

        return familyMembers;
    }

    private List<AppUser> buildAllUsersList(List<AppUser> familyMembers, AppUser mainUser) {
        List<AppUser> allUsers = new ArrayList<>(familyMembers);
        allUsers.add(mainUser);

        return allUsers;
    }

    private void validateNoActiveSubscriptions(List<AppUser> users, SubscriptionPlan plan) {
        for (AppUser user : users) {
            boolean hasActive = userSubscriptionRepository.findByUser(user).stream()
                .anyMatch(s -> s.getPlan().equals(plan) &&
                    s.getStatus() == SubscriptionStatus.ACTIVE);

            if (hasActive) {
                throw new IllegalStateException(
                    "User '%s' already has an active '%s' subscription. Cancellation required."
                        .formatted(user.getEmail(), plan.getName())
                );
            }
        }
    }

    private List<UserSubscription> createAndSaveSubscriptions(List<AppUser> users, SubscriptionPlan plan) {
        UserSubscription userSub = UserSubscription.builder()
            .plan(plan)
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now().plusDays(plan.getDuration()))
            .status(SubscriptionStatus.ACTIVE)
            .build();

        List<UserSubscription> newSubscriptions = users.stream()
            .map(user -> userSub.toBuilder().user(user).build())
            .toList();

        return userSubscriptionRepository.saveAll(newSubscriptions);
    }

    public Page<UserSubscription> getUserSubscriptions(AuthenticatedUser currentUser, Pageable pageable) {
        Long userId = currentUser.getAppUser().getId();
        AppUser user = userService.findById(userId);

        return userSubscriptionRepository.findAllByUser(user, pageable);
    }

    @Transactional
    public void cancelSubscription(Long subscriptionId, AuthenticatedUser currentUser) {
        Long userId = currentUser.getAppUser().getId();
        UserSubscription subscription = userSubscriptionUtils.findByIdWithLock(subscriptionId);

        validateUserOwnsSubscription(subscription, userId);

        if (!subscription.getStatus().equals(SubscriptionStatus.ACTIVE)) {
            throw new IllegalStateException("Only active subscriptions can be cancelled");
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        userSubscriptionRepository.save(subscription);
    }

    private static void validateUserOwnsSubscription(UserSubscription subscription, Long userId) {
        if (!subscription.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to cancel this subscription");
        }
    }
}
