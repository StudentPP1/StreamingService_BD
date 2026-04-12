package dev.studentpp1.streamingservice.subscription.application.command;

import java.util.List;

public record RemoveMoviesFromPlanCommand(Long id, List<Long> movieIds) {
}

