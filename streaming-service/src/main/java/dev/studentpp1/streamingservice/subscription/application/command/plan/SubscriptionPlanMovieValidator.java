package dev.studentpp1.streamingservice.subscription.application.command.plan;

import dev.studentpp1.streamingservice.subscription.domain.exception.MoviesNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionMovie;
import dev.studentpp1.streamingservice.subscription.domain.port.MovieProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubscriptionPlanMovieValidator {

    private final MovieProvider movieProvider;

    public Set<Long> validateAndGetMovieIds(List<Long> movieIds) {
        List<SubscriptionMovie> movies = movieProvider.findAllById(movieIds);
        if (movies.size() != new HashSet<>(movieIds).size()) {
            List<Long> foundIds = movies.stream().map(SubscriptionMovie::id).toList();
            List<Long> missingIds = movieIds.stream()
                    .filter(id -> !foundIds.contains(id)).toList();
            throw new MoviesNotFoundException(missingIds);
        }
        return movies.stream().map(SubscriptionMovie::id).collect(Collectors.toSet());
    }
}

