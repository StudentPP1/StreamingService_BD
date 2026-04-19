package dev.studentpp1.streamingservice.subscription.infrastructure.adapter;

import dev.studentpp1.streamingservice.movies.api.query.MovieView;
import dev.studentpp1.streamingservice.movies.api.query.MoviesQueryApi;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionMovie;
import dev.studentpp1.streamingservice.subscription.domain.port.MovieProvider;
import dev.studentpp1.streamingservice.subscription.internal.acl.SubscriptionMoviesAclTranslator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieProviderAdapter implements MovieProvider {

    private final MoviesQueryApi moviesQueryApi;
    private final SubscriptionMoviesAclTranslator aclTranslator;

    @Override
    public List<SubscriptionMovie> findAllById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<MovieView> movies = moviesQueryApi.findViewsById(ids);

        return movies.stream()
                .map(aclTranslator::toInternalMovie)
                .toList();
    }
}