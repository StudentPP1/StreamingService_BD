package dev.studentpp1.streamingservice.movies.domain.port;

import dev.studentpp1.streamingservice.movies.domain.model.Movie;

import java.util.List;

public interface MovieQueryPort {
    List<Movie> findAllById(List<Long> ids);
}
