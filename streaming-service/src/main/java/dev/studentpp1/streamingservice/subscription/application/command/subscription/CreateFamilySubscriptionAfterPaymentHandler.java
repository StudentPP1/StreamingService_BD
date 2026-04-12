package dev.studentpp1.streamingservice.subscription.application.command.subscription;

import dev.studentpp1.streamingservice.subscription.domain.exception.InvalidFamilyMemberException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriberContext;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.model.UserSubscription;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriberProvider;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateFamilySubscriptionAfterPaymentHandler {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriberProvider subscriberProvider;

    @Transactional
    public List<UserSubscription> handle(Long userId, String planName, List<String> memberEmails) {
        SubscriptionPlan plan = subscriptionPlanRepository.findByName(planName)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(planName));

        SubscriberContext mainUser = subscriberProvider.getById(userId);
        List<SubscriberContext> familyMembers = findFamilyMembers(memberEmails, mainUser);
        List<SubscriberContext> allUsers = new ArrayList<>(familyMembers);
        allUsers.add(mainUser);

        LocalDateTime now = LocalDateTime.now();
        List<UserSubscription> subscriptions = allUsers.stream()
                .map(user -> UserSubscription.create(user.id(), plan.getId(), now, plan.getDuration()))
                .toList();
        return userSubscriptionRepository.saveAll(subscriptions);
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
}

