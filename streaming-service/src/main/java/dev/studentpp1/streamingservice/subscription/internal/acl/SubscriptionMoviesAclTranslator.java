package dev.studentpp1.streamingservice.subscription.internal.acl;

import dev.studentpp1.streamingservice.movies.api.query.MovieView;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionMovie;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMoviesAclTranslator {

    public SubscriptionMovie toInternalMovie(MovieView movie) {
        return new SubscriptionMovie(
                movie.id(),
                movie.title(),
                movie.description(),
                movie.year(),
                movie.rating()
        );
    }
}

