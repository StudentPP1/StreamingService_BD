package dev.studentpp1.streamingservice.subscription.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SubscribeRequest(
    @NotNull
    @Positive
    Long planId
) {

}
