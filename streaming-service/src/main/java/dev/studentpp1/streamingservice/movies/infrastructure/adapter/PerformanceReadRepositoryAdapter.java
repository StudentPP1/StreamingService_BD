package dev.studentpp1.streamingservice.movies.infrastructure.adapter;

import dev.studentpp1.streamingservice.movies.application.query.performance.PerformanceReadRepository;
import dev.studentpp1.streamingservice.movies.application.query.performance.PerformanceReadModel;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.PerformanceEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.PerformanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PerformanceReadRepositoryAdapter implements PerformanceReadRepository {

    private final PerformanceJpaRepository performanceJpaRepository;

    @Override
    public Optional<PerformanceReadModel> findById(Long id) {
        return performanceJpaRepository.findById(id).map(this::toReadModel);
    }

    private PerformanceReadModel toReadModel(PerformanceEntity entity) {
        return new PerformanceReadModel(
                entity.getId(),
                entity.getCharacterName(),
                entity.getDescription(),
                entity.getActorEntity().getId(),
                entity.getMovieEntity().getId()
        );
    }
}

