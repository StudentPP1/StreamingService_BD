package dev.studentpp1.streamingservice.movies.application.query.movie;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.domain.exception.MovieNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieQueryHandler {

    private final MovieReadRepository movieReadRepository;

    public PageResult<MovieReadModel> handle(GetAllMoviesQuery query) {
        return movieReadRepository.findAll(query.page(), query.size());
    }

    public MovieReadModel handle(GetMovieByIdQuery query) {
        return movieReadRepository.findById(query.id())
                .orElseThrow(() -> new MovieNotFoundException(query.id()));
    }

    public MovieDetailsReadModel handle(GetMovieDetailsQuery query) {
        return movieReadRepository.findDetailsById(query.id())
                .orElseThrow(() -> new MovieNotFoundException(query.id()));
    }
}
