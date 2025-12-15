package dev.studentpp1.streamingservice.subscription.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SubscribeRequest(
    @NotNull
    @Positive
    Long planId
) {

}
