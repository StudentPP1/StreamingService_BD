package dev.studentpp1.streamingservice.movies.infrastructure.adapter;

import dev.studentpp1.streamingservice.movies.domain.exception.ActorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.MovieNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.model.Performance;
import dev.studentpp1.streamingservice.movies.domain.repository.PerformanceRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.ActorEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.MovieEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.PerformanceEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.mapper.PerformancePersistenceMapper;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.ActorJpaRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.MovieJpaRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.PerformanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PerformanceRepositoryAdapter implements PerformanceRepository {

    private final PerformanceJpaRepository jpaRepository;
    private final ActorJpaRepository actorJpaRepository;
    private final MovieJpaRepository movieJpaRepository;
    private final PerformancePersistenceMapper mapper;


    @Override
    public Performance save(Performance domain) {
        PerformanceEntity entity = mapper.toEntity(domain);
        ActorEntity actorEntity = actorJpaRepository
                .findById(domain.getActorId())
                .orElseThrow(() -> new ActorNotFoundException(domain.getActorId()));

        MovieEntity movieEntity = movieJpaRepository
                .findById(domain.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException(domain.getMovieId()));

        entity.setActorEntity(actorEntity);
        entity.setMovieEntity(movieEntity);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Performance> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Performance> findByMovieId(Long movieId) {
        return jpaRepository.findAllByMovieEntityId(movieId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Performance> findByActorId(Long actorId) {
        return jpaRepository.findAllByActorEntityId(actorId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
