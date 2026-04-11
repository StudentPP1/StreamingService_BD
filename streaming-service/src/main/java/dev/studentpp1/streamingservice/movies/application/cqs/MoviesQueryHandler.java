package dev.studentpp1.streamingservice.movies.application.cqs;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.application.cqs.MoviesCqs.*;
import dev.studentpp1.streamingservice.movies.application.usecase.ActorService;
import dev.studentpp1.streamingservice.movies.application.usecase.DirectorService;
import dev.studentpp1.streamingservice.movies.application.usecase.MovieService;
import dev.studentpp1.streamingservice.movies.application.usecase.PerformanceService;
import dev.studentpp1.streamingservice.movies.domain.model.Actor;
import dev.studentpp1.streamingservice.movies.domain.model.Director;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.domain.model.Performance;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.*;
import dev.studentpp1.streamingservice.movies.presentation.mapper.ActorPresentationMapper;
import dev.studentpp1.streamingservice.movies.presentation.mapper.DirectorPresentationMapper;
import dev.studentpp1.streamingservice.movies.presentation.mapper.MoviePresentationMapper;
import dev.studentpp1.streamingservice.movies.presentation.mapper.PerformancePresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoviesQueryHandler {
    private final MovieService movieService;
    private final ActorService actorService;
    private final DirectorService directorService;
    private final PerformanceService performanceService;
    private final MoviePresentationMapper movieMapper;
    private final ActorPresentationMapper actorMapper;
    private final DirectorPresentationMapper directorMapper;
    private final PerformancePresentationMapper performanceMapper;

    public PageResponse<MovieDto> handle(GetAllMoviesQuery query) {
        PageResult<Movie> result = movieService.getAllMovies(query.page(), query.size());
        return new PageResponse<>(
                result.content().stream().map(movieMapper::toDto).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    public MovieDto handle(GetMovieByIdQuery query) {
        return movieMapper.toDto(movieService.getMovieById(query.id()));
    }

    public MovieDetailDto handle(GetMovieDetailsQuery query) {
        return movieMapper.toDetailDto(movieService.getMovieDetails(query.id()));
    }

    public PageResponse<ActorDto> handle(GetAllActorsQuery query) {
        PageResult<Actor> result = actorService.getAllActors(query.page(), query.size());
        return new PageResponse<>(
                result.content().stream().map(actorMapper::toDto).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    public ActorDto handle(GetActorByIdQuery query) {
        return actorMapper.toDto(actorService.getActorById(query.id()));
    }

    public ActorDetailDto handle(GetActorDetailsQuery query) {
        return actorMapper.toDetailDto(actorService.getActorDetails(query.id()));
    }

    public PageResponse<DirectorDto> handle(GetAllDirectorsQuery query) {
        PageResult<Director> result = directorService.getAllDirectors(query.page(), query.size());
        return new PageResponse<>(
                result.content().stream().map(directorMapper::toDto).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    public DirectorDto handle(GetDirectorByIdQuery query) {
        return directorMapper.toDto(directorService.getDirectorById(query.id()));
    }

    public DirectorDetailDto handle(GetDirectorDetailsQuery query) {
        DirectorService.DirectorWithMovies result = directorService.getDirectorDetails(query.id());
        return directorMapper.toDetailDto(result.director(), movieMapper.toDtoList(result.movies()));
    }

    public PerformanceDto handle(GetPerformanceByIdQuery query) {
        Performance performance = performanceService.getPerformanceById(query.id());
        return performanceMapper.toDto(performance);
    }
}
