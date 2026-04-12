package dev.studentpp1.streamingservice.subscription.application.command;

import java.util.List;

public record AddMoviesToPlanCommand(Long id, List<Long> movieIds) {
}

