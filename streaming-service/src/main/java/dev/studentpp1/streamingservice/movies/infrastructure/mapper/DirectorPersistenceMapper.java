package dev.studentpp1.streamingservice.movies.infrastructure.mapper;

import dev.studentpp1.streamingservice.movies.domain.model.Director;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.DirectorEntity;
import org.springframework.stereotype.Component;

@Component
public class DirectorPersistenceMapper {

    public Director toDomain(DirectorEntity entity) {
        return Director.restore(
                entity.getId(),
                entity.getName(),
                entity.getSurname(),
                entity.getBiography()
        );
    }

    public DirectorEntity toEntity(Director domain) {
        DirectorEntity entity = new DirectorEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setSurname(domain.getSurname());
        entity.setBiography(domain.getBiography());
        return entity;
    }
}