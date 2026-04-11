package dev.studentpp1.streamingservice.movies.infrastructure.adapter;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.domain.exception.DirectorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.DirectorEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.MovieEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.mapper.MoviePersistenceMapper;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.DirectorJpaRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.MovieJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MovieRepositoryAdapter implements MovieRepository {

    private final MovieJpaRepository movieJpaRepository;
    private final DirectorJpaRepository directorJpaRepository;
    private final MoviePersistenceMapper mapper;

    public MovieRepositoryAdapter(MovieJpaRepository movieJpaRepository,
                                  DirectorJpaRepository directorJpaRepository,
                                  MoviePersistenceMapper mapper) {
        this.movieJpaRepository = movieJpaRepository;
        this.directorJpaRepository = directorJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Movie> findById(Long id) {
        return movieJpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Movie> findAll() {
        return movieJpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PageResult<Movie> findAll(int page, int size) {
        Page<MovieEntity> entityPage = movieJpaRepository
                .findAll(PageRequest.of(page, size));

        return new PageResult<>(
                entityPage.getContent().stream().map(mapper::toDomain).toList(),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }

    @Override
    public List<Movie> findAllByDirectorEntityId(Long directorId) {
        return movieJpaRepository.findAllByDirectorEntityId(directorId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Movie> findAllById(List<Long> movieIds) {
        return movieJpaRepository.findAllById(movieIds).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Movie save(Movie domain) {
        MovieEntity entity = mapper.toEntity(domain);

        if (domain.getDirectorId() != null) {
            DirectorEntity director = directorJpaRepository
                    .findById(domain.getDirectorId())
                    .orElseThrow(() -> new DirectorNotFoundException(domain.getDirectorId()));
            entity.setDirectorEntity(director);
        }

        return mapper.toDomain(movieJpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        movieJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return movieJpaRepository.existsById(id);
    }
}