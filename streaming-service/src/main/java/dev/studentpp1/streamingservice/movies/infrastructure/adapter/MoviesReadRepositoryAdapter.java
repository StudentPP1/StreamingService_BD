package dev.studentpp1.streamingservice.movies.infrastructure.adapter;

import dev.studentpp1.streamingservice.movies.application.read.MoviesReadRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.ActorEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.DirectorEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.MovieEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.ActorJpaRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.DirectorJpaRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.MovieJpaRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.PerformanceJpaRepository;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MoviesReadRepositoryAdapter implements MoviesReadRepository {

    private final MovieJpaRepository movieJpaRepository;
    private final ActorJpaRepository actorJpaRepository;
    private final DirectorJpaRepository directorJpaRepository;
    private final PerformanceJpaRepository performanceJpaRepository;

    @Override
    public PageResponse<MovieDto> findAllMovies(int page, int size) {
        var result = movieJpaRepository.findAll(PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(this::toMovieDto).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public Optional<MovieDto> findMovieById(Long id) {
        return movieJpaRepository.findById(id).map(this::toMovieDto);
    }

    @Override
    public Optional<MovieDetailDto> findMovieDetails(Long id) {
        return movieJpaRepository.findById(id).map(movie -> {
            DirectorDto director = toDirectorDto(movie.getDirectorEntity());
            List<MovieCastDto> cast = performanceJpaRepository.findAllByMovieEntityId(id).stream()
                    .map(p -> new MovieCastDto(
                            p.getActorEntity().getId(),
                            p.getActorEntity().getName(),
                            p.getActorEntity().getSurname(),
                            p.getCharacterName()))
                    .toList();
            return new MovieDetailDto(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDescription(),
                    movie.getYear(),
                    movie.getRating(),
                    director,
                    cast,
                    movie.getVersion()
            );
        });
    }

    @Override
    public PageResponse<ActorDto> findAllActors(int page, int size) {
        var result = actorJpaRepository.findAll(PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(this::toActorDto).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public Optional<ActorDto> findActorById(Long id) {
        return actorJpaRepository.findById(id).map(this::toActorDto);
    }

    @Override
    public Optional<ActorDetailDto> findActorDetails(Long id) {
        return actorJpaRepository.findById(id).map(actor -> {
            List<ActorFilmographyDto> filmography = performanceJpaRepository.findAllByActorEntityId(id).stream()
                    .map(p -> new ActorFilmographyDto(
                            p.getMovieEntity().getId(),
                            p.getMovieEntity().getTitle(),
                            p.getMovieEntity().getYear(),
                            p.getCharacterName()))
                    .toList();
            return new ActorDetailDto(
                    actor.getId(),
                    actor.getName(),
                    actor.getSurname(),
                    actor.getBiography(),
                    filmography
            );
        });
    }

    @Override
    public PageResponse<DirectorDto> findAllDirectors(int page, int size) {
        var result = directorJpaRepository.findAll(PageRequest.of(page, size));
        return new PageResponse<>(
                result.getContent().stream().map(this::toDirectorDto).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public Optional<DirectorDto> findDirectorById(Long id) {
        return directorJpaRepository.findById(id).map(this::toDirectorDto);
    }

    @Override
    public Optional<DirectorDetailDto> findDirectorDetails(Long id) {
        return directorJpaRepository.findById(id).map(director -> {
            List<MovieDto> movies = movieJpaRepository.findAllByDirectorEntityId(id).stream()
                    .map(this::toMovieDto)
                    .toList();
            return new DirectorDetailDto(
                    director.getId(),
                    director.getName(),
                    director.getSurname(),
                    director.getBiography(),
                    movies
            );
        });
    }

    @Override
    public Optional<PerformanceDto> findPerformanceById(Long id) {
        return performanceJpaRepository.findById(id)
                .map(p -> new PerformanceDto(
                        p.getId(),
                        p.getCharacterName(),
                        p.getDescription(),
                        p.getActorEntity().getId(),
                        p.getMovieEntity().getId()
                ));
    }

    private MovieDto toMovieDto(MovieEntity movie) {
        return new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getYear(),
                movie.getRating(),
                movie.getDirectorEntity().getId(),
                movie.getVersion()
        );
    }

    private ActorDto toActorDto(ActorEntity actor) {
        return new ActorDto(actor.getId(), actor.getName(), actor.getSurname(), actor.getBiography());
    }

    private DirectorDto toDirectorDto(DirectorEntity director) {
        return new DirectorDto(director.getId(), director.getName(), director.getSurname(), director.getBiography());
    }
}
