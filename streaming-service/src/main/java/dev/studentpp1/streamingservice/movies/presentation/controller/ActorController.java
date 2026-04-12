package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.command.ActorCommandHandler;
import dev.studentpp1.streamingservice.movies.application.command.actor.CreateActorCommand;
import dev.studentpp1.streamingservice.movies.application.command.actor.DeleteActorCommand;
import dev.studentpp1.streamingservice.movies.application.command.actor.UpdateActorCommand;
import dev.studentpp1.streamingservice.movies.application.query.actor.ActorDetailsReadModel;
import dev.studentpp1.streamingservice.movies.application.query.actor.ActorQueryHandler;
import dev.studentpp1.streamingservice.movies.application.query.actor.ActorReadModel;
import dev.studentpp1.streamingservice.movies.application.query.actor.GetActorByIdQuery;
import dev.studentpp1.streamingservice.movies.application.query.actor.GetActorDetailsQuery;
import dev.studentpp1.streamingservice.movies.application.query.actor.GetAllActorsQuery;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.presentation.dto.ActorCreateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorCommandHandler actorCommandHandler;
    private final ActorQueryHandler actorQueryHandler;

    public ActorController(ActorCommandHandler actorCommandHandler,
                           ActorQueryHandler actorQueryHandler) {
        this.actorCommandHandler = actorCommandHandler;
        this.actorQueryHandler = actorQueryHandler;
    }

    @GetMapping
    public ResponseEntity<PageResult<ActorReadModel>> getAllActors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(actorQueryHandler.handle(new GetAllActorsQuery(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorReadModel> getActorById(@PathVariable Long id) {
        return ResponseEntity.ok(actorQueryHandler.handle(new GetActorByIdQuery(id)));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ActorDetailsReadModel> getActorDetails(@PathVariable Long id) {
        return ResponseEntity.ok(actorQueryHandler.handle(new GetActorDetailsQuery(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createActor(@RequestBody @Valid ActorCreateRequest request) {
        actorCommandHandler.handle(new CreateActorCommand(
                request.name(),
                request.surname(),
                request.biography()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateActor(@PathVariable Long id,
                                            @RequestBody @Valid ActorCreateRequest request) {
        actorCommandHandler.handle(new UpdateActorCommand(
                id,
                request.name(),
                request.surname(),
                request.biography()
        ));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id) {
        actorCommandHandler.handle(new DeleteActorCommand(id));
        return ResponseEntity.noContent().build();
    }
}
