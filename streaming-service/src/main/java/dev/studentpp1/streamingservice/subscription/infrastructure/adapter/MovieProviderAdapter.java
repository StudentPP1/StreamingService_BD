package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.domain.port.MovieQueryPort;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionMovie;
import dev.studentpp1.streamingservice.subscription.domain.port.MovieProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieProviderAdapter implements MovieProvider {

    private final MovieQueryPort movieQueryPort;

    @Override
    public List<SubscriptionMovie> findAllById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<Movie> movies = movieQueryPort.findAllById(ids);

        return movies.stream()
                .map(m -> new SubscriptionMovie(
                        m.getId(),
                        m.getTitle(),
                        m.getDescription(),
                        m.getYear(),
                        m.getRating()
                ))
                .toList();
    }
}