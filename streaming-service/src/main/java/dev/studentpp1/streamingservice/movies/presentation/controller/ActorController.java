package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.cqs.MoviesCqs.*;
import dev.studentpp1.streamingservice.movies.application.cqs.MoviesCommandHandler;
import dev.studentpp1.streamingservice.movies.application.cqs.MoviesQueryHandler;
import dev.studentpp1.streamingservice.movies.presentation.dto.request.ActorCreateRequest;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.ActorDetailDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.ActorDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
public class ActorController {

    private final MoviesCommandHandler movieCommandHandler;
    private final MoviesQueryHandler movieQueryHandler;

    @GetMapping
    public ResponseEntity<PageResponse<ActorDto>> getAllActors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(movieQueryHandler.handle(new GetAllActorsQuery(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorDto> getActorById(@PathVariable Long id) {
        return ResponseEntity.ok(movieQueryHandler.handle(new GetActorByIdQuery(id)));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ActorDetailDto> getActorDetails(@PathVariable Long id) {
        return ResponseEntity.ok(movieQueryHandler.handle(new GetActorDetailsQuery(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActorDto> createActor(@RequestBody @Valid ActorCreateRequest request) {
        Long id = movieCommandHandler.handle(new CreateActorCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(movieQueryHandler.handle(new GetActorByIdQuery(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActorDto> updateActor(@PathVariable Long id,
                                                @RequestBody @Valid ActorCreateRequest request) {
        movieCommandHandler.handle(new UpdateActorCommand(id, request));
        return ResponseEntity.ok(movieQueryHandler.handle(new GetActorByIdQuery(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id) {
        movieCommandHandler.handle(new DeleteActorCommand(id));
        return ResponseEntity.noContent().build();
    }
}