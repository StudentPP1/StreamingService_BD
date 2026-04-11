package dev.studentpp1.streamingservice.movies.application.read;

import dev.studentpp1.streamingservice.movies.presentation.dto.response.*;

import java.util.Optional;

public interface MoviesReadRepository {
    PageResponse<MovieDto> findAllMovies(int page, int size);

    Optional<MovieDto> findMovieById(Long id);

    Optional<MovieDetailDto> findMovieDetails(Long id);

    PageResponse<ActorDto> findAllActors(int page, int size);

    Optional<ActorDto> findActorById(Long id);

    Optional<ActorDetailDto> findActorDetails(Long id);

    PageResponse<DirectorDto> findAllDirectors(int page, int size);

    Optional<DirectorDto> findDirectorById(Long id);

    Optional<DirectorDetailDto> findDirectorDetails(Long id);

    Optional<PerformanceDto> findPerformanceById(Long id);
}

