package dev.studentpp1.streamingservice.movies.application.cqs;

import dev.studentpp1.streamingservice.movies.application.cqs.MoviesCqs.*;
import dev.studentpp1.streamingservice.movies.application.read.MoviesReadRepository;
import dev.studentpp1.streamingservice.movies.domain.exception.ActorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.DirectorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.MovieNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.PerformanceNotFoundException;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MoviesQueryHandler {
    private final MoviesReadRepository moviesReadRepository;

    public PageResponse<MovieDto> handle(GetAllMoviesQuery query) {
        return moviesReadRepository.findAllMovies(query.page(), query.size());
    }

    public MovieDto handle(GetMovieByIdQuery query) {
        return moviesReadRepository.findMovieById(query.id())
                .orElseThrow(() -> new MovieNotFoundException(query.id()));
    }

    public MovieDetailDto handle(GetMovieDetailsQuery query) {
        return moviesReadRepository.findMovieDetails(query.id())
                .orElseThrow(() -> new MovieNotFoundException(query.id()));
    }

    public PageResponse<ActorDto> handle(GetAllActorsQuery query) {
        return moviesReadRepository.findAllActors(query.page(), query.size());
    }

    public ActorDto handle(GetActorByIdQuery query) {
        return moviesReadRepository.findActorById(query.id())
                .orElseThrow(() -> new ActorNotFoundException(query.id()));
    }

    public ActorDetailDto handle(GetActorDetailsQuery query) {
        return moviesReadRepository.findActorDetails(query.id())
                .orElseThrow(() -> new ActorNotFoundException(query.id()));
    }

    public PageResponse<DirectorDto> handle(GetAllDirectorsQuery query) {
        return moviesReadRepository.findAllDirectors(query.page(), query.size());
    }

    public DirectorDto handle(GetDirectorByIdQuery query) {
        return moviesReadRepository.findDirectorById(query.id())
                .orElseThrow(() -> new DirectorNotFoundException(query.id()));
    }

    public DirectorDetailDto handle(GetDirectorDetailsQuery query) {
        return moviesReadRepository.findDirectorDetails(query.id())
                .orElseThrow(() -> new DirectorNotFoundException(query.id()));
    }

    public PerformanceDto handle(GetPerformanceByIdQuery query) {
        return moviesReadRepository.findPerformanceById(query.id())
                .orElseThrow(() -> new PerformanceNotFoundException(query.id()));
    }
}
