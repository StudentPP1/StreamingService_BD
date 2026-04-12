package dev.studentpp1.streamingservice.movies.application.usecase;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.domain.exception.ActorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.DirectorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.MovieNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.OptimisticLockingException;
import dev.studentpp1.streamingservice.movies.domain.factory.MovieFactory;
import dev.studentpp1.streamingservice.movies.domain.model.*;
import dev.studentpp1.streamingservice.movies.domain.repository.ActorRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.DirectorRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.PerformanceRepository;
import dev.studentpp1.streamingservice.movies.application.dto.MovieCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;
    private final PerformanceRepository performanceRepository;
    private final ActorRepository actorRepository;
    private final MovieFactory movieFactory;

    @Transactional(readOnly = true)
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public MovieDetails getMovieDetails(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        Director director = directorRepository.findById(movie.getDirectorId())
                .orElseThrow(() -> new DirectorNotFoundException(movie.getDirectorId()));

        List<Performance> performances = performanceRepository.findByMovieId(id);

        List<MovieCastItem> cast = performances.stream()
                .map(p -> {
                    Actor actor = actorRepository.findById(p.getActorId())
                            .orElseThrow(() -> new ActorNotFoundException(p.getActorId()));
                    return new MovieCastItem(p, actor);
                })
                .toList();

        return new MovieDetails(movie, director, cast);
    }

    @Transactional(readOnly = true)
    public PageResult<Movie> getAllMovies(int page, int size) {
        return movieRepository.findAll(page, size);
    }

    @Transactional
    public Movie createMovie(MovieCreateRequest request) {
        Movie movie = movieFactory.create(
                request.title(),
                request.description(),
                request.year(),
                request.rating(),
                request.directorId()
        );
        return movieRepository.save(movie);
    }

    @Transactional
    public Movie updateMovie(Long id, MovieCreateRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        if (request.version() != null && !request.version().equals(movie.getVersion())) {
            throw new OptimisticLockingException(
                    "Movie data was modified by another user. Please refresh."
            );
        }

        movie.update(
                request.title(),
                request.description(),
                request.year(),
                request.rating(),
                request.directorId()
        );

        return movieRepository.save(movie);
    }

    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException(id);
        }
        movieRepository.deleteById(id);
    }

    public List<Movie> findAllById(List<Long> ids) {
        return movieRepository.findAllById(ids);
    }

    public record MovieDetails(Movie movie, Director director, List<MovieCastItem> cast) {
    }

    public record MovieCastItem(Performance performance, Actor actor) {
    }

}