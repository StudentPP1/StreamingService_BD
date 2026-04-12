package dev.studentpp1.streamingservice.subscription.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.subscription.domain.exception.*;
import dev.studentpp1.streamingservice.subscription.domain.factory.UserSubscriptionFactory;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutCommand;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutResult;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriberContext;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriberProvider;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionPaymentGateway;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import dev.studentpp1.streamingservice.subscription.application.dto.CreateFamilySubscriptionRequest;
import dev.studentpp1.streamingservice.subscription.application.dto.SubscribeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@PreAuthorize("isAuthenticated()")
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    public static final String FAMILY_MEMBER_EMAILS_KEY = "familyMemberEmails";
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriberProvider subscriberProvider;
    private final SubscriptionPaymentGateway paymentGateway;
    private final UserSubscriptionFactory userSubscriptionFactory;
    private final ObjectMapper objectMapper;

    @Transactional
    public UserSubscription createUserSubscription(String planName, Long userId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findByName(planName)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(planName));
        UserSubscription subscription = userSubscriptionFactory.create(
                userId, plan.getId(), LocalDateTime.now(), plan.getDuration());
        return userSubscriptionRepository.save(subscription);
    }

    @Transactional
    public CheckoutResult subscribeUser(SubscribeRequest request, Long userId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.planId())
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(request.planId()));
        validateNoActiveSubscriptions(List.of(userId), plan);
        return paymentGateway.generateCheckout(new CheckoutCommand(plan.getName(), plan.getPrice(), userId, new HashMap<>()));
    }

    @Transactional
    public CheckoutResult createFamilySubscription(CreateFamilySubscriptionRequest request, Long userId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.planId())
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(request.planId()));

        SubscriberContext mainUser = subscriberProvider.getById(userId);
        List<SubscriberContext> familyMembers = findFamilyMembers(request.memberEmails(), mainUser);
        List<SubscriberContext> allUsers = buildAllUsersList(familyMembers, mainUser);

        validateNoActiveSubscriptions(allUsers.stream().map(SubscriberContext::id).toList(), plan);

        String memberEmailsJson;
        try {
            memberEmailsJson = objectMapper.writeValueAsString(request.memberEmails());
        } catch (JsonProcessingException e) {
            throw new SerializationException("Failed to serialize family member emails", e);
        }

        Map<String, String> metadata = Map.of(FAMILY_MEMBER_EMAILS_KEY, memberEmailsJson);
        return paymentGateway.generateCheckout(new CheckoutCommand(plan.getName(), plan.getPrice(), userId, metadata));
    }

    @Transactional
    public List<UserSubscription> createFamilySubscriptionAfterPayment(
            Long userId, String planName, List<String> memberEmails) {
        SubscriptionPlan plan = subscriptionPlanRepository.findByName(planName)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(planName));

        SubscriberContext mainUser = subscriberProvider.getById(userId);
        List<SubscriberContext> familyMembers = findFamilyMembers(memberEmails, mainUser);
        List<SubscriberContext> allUsers = buildAllUsersList(familyMembers, mainUser);

        return createAndSaveSubscriptions(allUsers, plan);
    }

    @Transactional(readOnly = true)
    public PageResult<UserSubscription> getUserSubscriptions(
            Long userId, int page, int size) {
        return userSubscriptionRepository.findAllByUserId(userId, page, size);
    }

    @Transactional
    public void cancelSubscription(Long subscriptionId, Long userId) {
        UserSubscription subscription = userSubscriptionRepository
                .findByIdWithLock(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));

        if (!subscription.getUserId().equals(userId)) {
            throw new SubscriptionAccessDeniedException();
        }

        subscription.cancel();
        userSubscriptionRepository.save(subscription);
    }

    private List<SubscriberContext> findFamilyMembers(List<String> memberEmails, SubscriberContext mainUser) {
        List<SubscriberContext> familyMembers = new ArrayList<>();
        for (String email : memberEmails) {
            SubscriberContext member = subscriberProvider.getByEmail(email);
            if (member.id().equals(mainUser.id())) {
                throw new InvalidFamilyMemberException();
            }
            familyMembers.add(member);
        }
        return familyMembers;
    }

    private List<SubscriberContext> buildAllUsersList(List<SubscriberContext> familyMembers, SubscriberContext mainUser) {
        List<SubscriberContext> allUsers = new ArrayList<>(familyMembers);
        allUsers.add(mainUser);
        return allUsers;
    }

    private void validateNoActiveSubscriptions(List<Long> userIds, SubscriptionPlan plan) {
        boolean hasActive = userSubscriptionRepository.existsByUserIdInAndPlanIdAndStatus(
                userIds, plan.getId(), SubscriptionStatus.ACTIVE);
        if (hasActive) {
            throw new ActiveSubscriptionAlreadyExistsException("Someone in the provided list already has an active plan: " + plan.getName());
        }
    }

    private List<UserSubscription> createAndSaveSubscriptions(
            List<SubscriberContext> users, SubscriptionPlan plan) {
        LocalDateTime now = LocalDateTime.now();
        List<UserSubscription> subscriptions = users.stream()
                .map(user -> UserSubscription.create(
                        user.id(), plan.getId(), now, plan.getDuration()))
                .toList();
        return userSubscriptionRepository.saveAll(subscriptions);
    }

    @Transactional(readOnly = true)
    public PageResult<UserSubscriptionWithPlan> getUserSubscriptionsWithPlan(
            Long userId, int page, int size) {
        PageResult<UserSubscription> result =
                userSubscriptionRepository.findAllByUserId(userId, page, size);

        List<UserSubscriptionWithPlan> content = result.content().stream()
                .map(sub -> {
                    String planName = subscriptionPlanRepository
                            .findById(sub.getPlanId())
                            .map(SubscriptionPlan::getName)
                            .orElse("Unknown");
                    return new UserSubscriptionWithPlan(sub, planName);
                })
                .toList();

        return new PageResult<>(content, result.page(),
                result.size(), result.totalElements(), result.totalPages());
    }
    public record UserSubscriptionWithPlan(UserSubscription subscription, String planName) {
    }
}