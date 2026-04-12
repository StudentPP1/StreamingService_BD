package dev.studentpp1.streamingservice.movies.infrastructure.adapter;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.application.query.director.DirectorReadRepository;
import dev.studentpp1.streamingservice.movies.application.query.director.DirectorDetailsReadModel;
import dev.studentpp1.streamingservice.movies.application.query.director.DirectorReadModel;
import dev.studentpp1.streamingservice.movies.application.query.movie.MovieReadModel;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.DirectorEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.DirectorJpaRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.MovieJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DirectorReadRepositoryAdapter implements DirectorReadRepository {

    private final DirectorJpaRepository directorJpaRepository;
    private final MovieJpaRepository movieJpaRepository;

    @Override
    public PageResult<DirectorReadModel> findAll(int page, int size) {
        Page<DirectorEntity> result = directorJpaRepository.findAll(PageRequest.of(page, size));
        return new PageResult<>(
                result.getContent().stream().map(this::toDirectorReadModel).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public Optional<DirectorReadModel> findById(Long id) {
        return directorJpaRepository.findById(id).map(this::toDirectorReadModel);
    }

    @Override
    public Optional<DirectorDetailsReadModel> findDetailsById(Long id) {
        return directorJpaRepository.findById(id).map(director -> {
            var movies = movieJpaRepository.findAllByDirectorEntityId(id).stream()
                    .map(movie -> new MovieReadModel(
                            movie.getId(),
                            movie.getTitle(),
                            movie.getDescription(),
                            movie.getYear(),
                            movie.getRating(),
                            movie.getDirectorEntity().getId(),
                            movie.getVersion()
                    ))
                    .toList();
            return new DirectorDetailsReadModel(
                    director.getId(),
                    director.getName(),
                    director.getSurname(),
                    director.getBiography(),
                    movies
            );
        });
    }

    private DirectorReadModel toDirectorReadModel(DirectorEntity entity) {
        return new DirectorReadModel(
                entity.getId(),
                entity.getName(),
                entity.getSurname(),
                entity.getBiography()
        );
    }
}

