package dev.studentpp1.streamingservice.movies.application.query.actor;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.domain.exception.ActorNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActorQueryHandler {

    private final ActorReadRepository actorReadRepository;

    public PageResult<ActorReadModel> handle(GetAllActorsQuery query) {
        return actorReadRepository.findAll(query.page(), query.size());
    }

    public ActorReadModel handle(GetActorByIdQuery query) {
        return actorReadRepository.findById(query.id())
                .orElseThrow(() -> new ActorNotFoundException(query.id()));
    }

    public ActorDetailsReadModel handle(GetActorDetailsQuery query) {
        return actorReadRepository.findDetailsById(query.id())
                .orElseThrow(() -> new ActorNotFoundException(query.id()));
    }
}
