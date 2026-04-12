package dev.studentpp1.streamingservice.movies.application.query;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.application.usecase.MovieService;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieQueryHandler {

    private final MovieService movieService;

    public PageResult<Movie> handle(GetAllMoviesQuery query) {
        return movieService.getAllMovies(query.page(), query.size());
    }

    public Movie handle(GetMovieByIdQuery query) {
        return movieService.getMovieById(query.id());
    }

    public MovieService.MovieDetails handle(GetMovieDetailsQuery query) {
        return movieService.getMovieDetails(query.id());
    }
}

