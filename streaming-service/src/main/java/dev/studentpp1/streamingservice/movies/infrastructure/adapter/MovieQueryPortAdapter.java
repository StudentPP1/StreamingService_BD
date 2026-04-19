package dev.studentpp1.streamingservice.movies.infrastructure.adapter;

import dev.studentpp1.streamingservice.movies.api.query.MovieView;
import dev.studentpp1.streamingservice.movies.api.query.MoviesQueryApi;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieQueryPortAdapter implements MoviesQueryApi {

    private final MovieRepository movieRepository;

    @Override
    public List<MovieView> findViewsById(List<Long> ids) {
        return movieRepository.findAllById(ids).stream()
                .map(movie -> new MovieView(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getDescription(),
                        movie.getYear(),
                        movie.getRating()
                ))
                .toList();
    }
}
