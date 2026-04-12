package dev.studentpp1.streamingservice.movies.infrastructure.adapter;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.application.query.movie.MovieReadRepository;
import dev.studentpp1.streamingservice.movies.application.query.movie.MovieReadModel;
import dev.studentpp1.streamingservice.movies.application.query.movie.MovieDetailsReadModel;
import dev.studentpp1.streamingservice.movies.application.query.movie.MovieCastItemReadModel;
import dev.studentpp1.streamingservice.movies.application.query.director.DirectorReadModel;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.MovieEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.PerformanceEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.MovieJpaRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.PerformanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MovieReadRepositoryAdapter implements MovieReadRepository {

    private final MovieJpaRepository movieJpaRepository;
    private final PerformanceJpaRepository performanceJpaRepository;

    @Override
    public PageResult<MovieReadModel> findAll(int page, int size) {
        Page<MovieEntity> result = movieJpaRepository.findAll(PageRequest.of(page, size));
        return new PageResult<>(
                result.getContent().stream().map(this::toMovieReadModel).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public Optional<MovieReadModel> findById(Long id) {
        return movieJpaRepository.findById(id).map(this::toMovieReadModel);
    }

    @Override
    public Optional<MovieDetailsReadModel> findDetailsById(Long id) {
        return movieJpaRepository.findById(id).map(movie -> {
            var cast = performanceJpaRepository.findAllByMovieEntityId(id).stream()
                    .map(this::toCastItem)
                    .toList();
            DirectorReadModel director = new DirectorReadModel(
                    movie.getDirectorEntity().getId(),
                    movie.getDirectorEntity().getName(),
                    movie.getDirectorEntity().getSurname(),
                    movie.getDirectorEntity().getBiography()
            );
            return new MovieDetailsReadModel(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDescription(),
                    movie.getYear(),
                    movie.getRating(),
                    movie.getVersion(),
                    director,
                    cast
            );
        });
    }

    private MovieReadModel toMovieReadModel(MovieEntity entity) {
        return new MovieReadModel(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getYear(),
                entity.getRating(),
                entity.getDirectorEntity().getId(),
                entity.getVersion()
        );
    }

    private MovieCastItemReadModel toCastItem(PerformanceEntity entity) {
        return new MovieCastItemReadModel(
                entity.getActorEntity().getId(),
                entity.getActorEntity().getName(),
                entity.getActorEntity().getSurname(),
                entity.getCharacterName()
        );
    }
}

