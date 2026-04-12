package dev.studentpp1.streamingservice.subscription.application.command.plan;

import java.math.BigDecimal;
import java.util.List;

public record UpdatePlanCommand(Long id, String name, String description, BigDecimal price,
								Integer duration, List<Long> includedMovieIds) {
}

