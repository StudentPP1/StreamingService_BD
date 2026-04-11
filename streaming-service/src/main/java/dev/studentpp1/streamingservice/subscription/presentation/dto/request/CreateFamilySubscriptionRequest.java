package dev.studentpp1.streamingservice.subscription.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateFamilySubscriptionRequest(
    @NotNull(message = "Plan ID is required")
    Long planId,

    @NotEmpty(message = "Family member emails are required")
     @Size(max = 4, message = "Maximum 4 family members allowed")
    List<@Email(message = "Invalid email format") String> memberEmails
) {}
