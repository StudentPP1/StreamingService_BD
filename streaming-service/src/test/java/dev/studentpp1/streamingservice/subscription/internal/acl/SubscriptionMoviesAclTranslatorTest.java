package dev.studentpp1.streamingservice.subscription.internal.acl;

import dev.studentpp1.streamingservice.movies.api.query.MovieView;
import dev.studentpp1.streamingservice.subscription.domain.model.SubscriptionMovie;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionMoviesAclTranslatorTest {

    private final SubscriptionMoviesAclTranslator translator = new SubscriptionMoviesAclTranslator();

    @Test
    void mapsMovieViewToInternalSubscriptionMovie() {
        MovieView view = new MovieView(11L, "Dune", "Sci-fi", 2021, BigDecimal.valueOf(8.8));

        SubscriptionMovie movie = translator.toInternalMovie(view);

        assertThat(movie.id()).isEqualTo(11L);
        assertThat(movie.title()).isEqualTo("Dune");
        assertThat(movie.description()).isEqualTo("Sci-fi");
        assertThat(movie.year()).isEqualTo(2021);
        assertThat(movie.rating()).isEqualByComparingTo("8.8");
    }
}

