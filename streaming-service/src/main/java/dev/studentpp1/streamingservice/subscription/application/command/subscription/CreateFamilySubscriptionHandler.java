package dev.studentpp1.streamingservice.subscription.application.command.subscription;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.studentpp1.streamingservice.subscription.domain.exception.ActiveSubscriptionAlreadyExistsException;
import dev.studentpp1.streamingservice.subscription.domain.exception.InvalidFamilyMemberException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SerializationException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.model.CheckoutCommand;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriberContext;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionStatus;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriberProvider;
import dev.studentpp1.streamingservice.subscription.domain.port.SubscriptionPaymentGateway;
import dev.studentpp1.streamingservice.subscription.domain.repository.SubscriptionPlanRepository;
import dev.studentpp1.streamingservice.subscription.domain.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CreateFamilySubscriptionHandler {

    public static final String FAMILY_MEMBER_EMAILS_KEY = "familyMemberEmails";

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriberProvider subscriberProvider;
    private final SubscriptionPaymentGateway paymentGateway;
    private final ObjectMapper objectMapper;

    @Transactional
    public void handle(CreateFamilySubscriptionCommand command) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(command.planId())
                .orElseThrow(() -> new SubscriptionPlanNotFoundException(command.planId()));

        SubscriberContext mainUser = subscriberProvider.getById(command.userId());
        List<SubscriberContext> familyMembers = findFamilyMembers(command.memberEmails(), mainUser);
        List<SubscriberContext> allUsers = new ArrayList<>(familyMembers);
        allUsers.add(mainUser);

        boolean hasActive = userSubscriptionRepository.existsByUserIdInAndPlanIdAndStatus(
                allUsers.stream().map(SubscriberContext::id).toList(),
                plan.getId(),
                SubscriptionStatus.ACTIVE
        );
        if (hasActive) {
            throw new ActiveSubscriptionAlreadyExistsException(
                    "Someone in the provided list already has an active plan: " + plan.getName());
        }

        String memberEmailsJson;
        try {
            memberEmailsJson = objectMapper.writeValueAsString(command.memberEmails());
        } catch (JsonProcessingException e) {
            throw new SerializationException("Failed to serialize family member emails", e);
        }

        Map<String, String> metadata = Map.of(FAMILY_MEMBER_EMAILS_KEY, memberEmailsJson);
        paymentGateway.generateCheckout(new CheckoutCommand(
                plan.getName(),
                plan.getPrice(),
                command.userId(),
                metadata
        ));
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

