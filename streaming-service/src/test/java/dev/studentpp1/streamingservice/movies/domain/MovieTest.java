package dev.studentpp1.streamingservice.movies.domain;

import dev.studentpp1.streamingservice.movies.domain.exception.InvalidRatingException;
import dev.studentpp1.streamingservice.movies.domain.exception.MovieDomainException;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class MovieTest {

    @Test
    void create_validMovie_success() {
        Movie movie = Movie.create("Inception", "desc", 2010,
                BigDecimal.valueOf(8.8), 1L);

        assertThat(movie.getTitle()).isEqualTo("Inception");
        assertThat(movie.getId()).isNull();
    }

    @Test
    void create_blankTitle_throwsDomainException() {
        assertThatThrownBy(() ->
                Movie.create("", "desc", 2010, BigDecimal.valueOf(8.8), 1L))
                .isInstanceOf(MovieDomainException.class);
    }

    @Test
    void create_invalidYear_throwsDomainException() {
        assertThatThrownBy(() ->
                Movie.create("Inception", "desc", 1800, BigDecimal.valueOf(8.8), 1L))
                .isInstanceOf(MovieDomainException.class);
    }

    @Test
    void create_ratingAboveTen_throwsInvalidRatingException() {
        assertThatThrownBy(() ->
                Movie.create("Inception", "desc", 2010, BigDecimal.valueOf(11.0), 1L))
                .isInstanceOf(InvalidRatingException.class);
    }

    @Test
    void create_negativeRating_throwsInvalidRatingException() {
        assertThatThrownBy(() ->
                Movie.create("Inception", "desc", 2010, BigDecimal.valueOf(-1.0), 1L))
                .isInstanceOf(InvalidRatingException.class);
    }

    @Test
    void update_validData_success() {
        Movie movie = Movie.restore(1L, "Inception", "desc", 2010,
                BigDecimal.valueOf(8.8), 1L, 0L);

        movie.update("Interstellar", "new desc", 2014,
                BigDecimal.valueOf(9.0), 2L);

        assertThat(movie.getTitle()).isEqualTo("Interstellar");
        assertThat(movie.getRating()).isEqualByComparingTo(BigDecimal.valueOf(9.0));
    }

    @Test
    void update_invalidRating_throwsException() {
        Movie movie = Movie.restore(1L, "Inception", "desc", 2010,
                BigDecimal.valueOf(8.8), 1L, 0L);

        assertThatThrownBy(() ->
                movie.update("Inception", "desc", 2010, BigDecimal.valueOf(15.0), 1L))
                .isInstanceOf(InvalidRatingException.class);
    }
}