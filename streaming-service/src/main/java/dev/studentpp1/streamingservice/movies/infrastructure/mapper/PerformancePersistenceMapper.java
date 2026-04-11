package dev.studentpp1.streamingservice.movies.infrastructure.mapper;

import dev.studentpp1.streamingservice.movies.domain.model.Performance;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.PerformanceEntity;
import org.springframework.stereotype.Component;

@Component
public class PerformancePersistenceMapper {

    public Performance toDomain(PerformanceEntity entity) {
        return Performance.restore(
                entity.getId(),
                entity.getMovieEntity() != null ? entity.getMovieEntity().getId() : null,
                entity.getActorEntity() != null ? entity.getActorEntity().getId() : null,
                entity.getCharacterName(),
                entity.getDescription()
        );
    }

    public PerformanceEntity toEntity(Performance domain) {
        PerformanceEntity entity = new PerformanceEntity();
        entity.setId(domain.getId());
        entity.setCharacterName(domain.getCharacterName());
        entity.setDescription(domain.getDescription());
        return entity;
    }
}