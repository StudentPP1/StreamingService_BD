package dev.studentpp1.streamingservice.movies.infrastructure.adapter;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.application.query.actor.ActorReadRepository;
import dev.studentpp1.streamingservice.movies.application.query.actor.ActorDetailsReadModel;
import dev.studentpp1.streamingservice.movies.application.query.actor.ActorFilmographyItemReadModel;
import dev.studentpp1.streamingservice.movies.application.query.actor.ActorReadModel;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.ActorEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.entity.PerformanceEntity;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.ActorJpaRepository;
import dev.studentpp1.streamingservice.movies.infrastructure.repository.PerformanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ActorReadRepositoryAdapter implements ActorReadRepository {

    private final ActorJpaRepository actorJpaRepository;
    private final PerformanceJpaRepository performanceJpaRepository;

    @Override
    public PageResult<ActorReadModel> findAll(int page, int size) {
        Page<ActorEntity> result = actorJpaRepository.findAll(PageRequest.of(page, size));
        return new PageResult<>(
                result.getContent().stream().map(this::toActorReadModel).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public Optional<ActorReadModel> findById(Long id) {
        return actorJpaRepository.findById(id).map(this::toActorReadModel);
    }

    @Override
    public Optional<ActorDetailsReadModel> findDetailsById(Long id) {
        return actorJpaRepository.findById(id).map(actor -> {
            var filmography = performanceJpaRepository.findAllByActorEntityId(id).stream()
                    .map(this::toFilmographyItem)
                    .toList();
            return new ActorDetailsReadModel(
                    actor.getId(),
                    actor.getName(),
                    actor.getSurname(),
                    actor.getBiography(),
                    filmography
            );
        });
    }

    private ActorReadModel toActorReadModel(ActorEntity entity) {
        return new ActorReadModel(
                entity.getId(),
                entity.getName(),
                entity.getSurname(),
                entity.getBiography()
        );
    }

    private ActorFilmographyItemReadModel toFilmographyItem(PerformanceEntity entity) {
        return new ActorFilmographyItemReadModel(
                entity.getMovieEntity().getId(),
                entity.getMovieEntity().getTitle(),
                entity.getMovieEntity().getYear(),
                entity.getCharacterName()
        );
    }
}

