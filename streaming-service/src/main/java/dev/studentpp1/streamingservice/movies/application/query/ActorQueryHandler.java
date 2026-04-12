package dev.studentpp1.streamingservice.movies.application.query;

import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.application.usecase.ActorService;
import dev.studentpp1.streamingservice.movies.domain.model.Actor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActorQueryHandler {

    private final ActorService actorService;

    public PageResult<Actor> handle(GetAllActorsQuery query) {
        return actorService.getAllActors(query.page(), query.size());
    }

    public Actor handle(GetActorByIdQuery query) {
        return actorService.getActorById(query.id());
    }

    public ActorService.ActorDetails handle(GetActorDetailsQuery query) {
        return actorService.getActorDetails(query.id());
    }
}

