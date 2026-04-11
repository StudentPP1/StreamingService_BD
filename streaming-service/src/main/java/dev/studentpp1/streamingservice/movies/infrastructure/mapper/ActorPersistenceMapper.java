package dev.studentpp1.streamingservice.movies.infrastructure.mapper;

import dev.studentpp1.streamingservice.movies.domain.model.Actor;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.ActorEntity;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
public class ActorPersistenceMapper {

    public Actor toDomain(ActorEntity entity) {
        return Actor.restore(
                entity.getId(),
                entity.getName(),
                entity.getSurname(),
                entity.getBiography()
        );
    }

    public ActorEntity toEntity(Actor domain) {
        ActorEntity entity = new ActorEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setSurname(domain.getSurname());
        entity.setBiography(domain.getBiography());
        return entity;
    }
}