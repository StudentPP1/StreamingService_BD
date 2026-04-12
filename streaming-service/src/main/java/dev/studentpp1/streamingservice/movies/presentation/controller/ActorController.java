package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.usecase.ActorService;
import dev.studentpp1.streamingservice.movies.domain.model.Actor;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.presentation.dto.ActorDetailDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.ActorDto;
import dev.studentpp1.streamingservice.movies.application.dto.ActorCreateRequest;
import dev.studentpp1.streamingservice.movies.presentation.dto.PageResponse;
import dev.studentpp1.streamingservice.movies.presentation.mapper.ActorPresentationMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorService actorService;
    private final ActorPresentationMapper actorMapper;

    public ActorController(ActorService actorService,
                           ActorPresentationMapper actorMapper) {
        this.actorService = actorService;
        this.actorMapper = actorMapper;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ActorDto>> getAllActors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<Actor> result = actorService.getAllActors(page, size);
        PageResponse<ActorDto> response = new PageResponse<>(
                result.content().stream().map(actorMapper::toDto).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorDto> getActorById(@PathVariable Long id) {
        Actor actor = actorService.getActorById(id);
        return ResponseEntity.ok(actorMapper.toDto(actor));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ActorDetailDto> getActorDetails(@PathVariable Long id) {
        return ResponseEntity.ok(actorMapper.toDetailDto(actorService.getActorDetails(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActorDto> createActor(@RequestBody @Valid ActorCreateRequest request) {
        Actor actor = actorService.createActor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(actorMapper.toDto(actor));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActorDto> updateActor(@PathVariable Long id,
                                                @RequestBody @Valid ActorCreateRequest request) {
        Actor actor = actorService.updateActor(id, request);
        return ResponseEntity.ok(actorMapper.toDto(actor));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id) {
        actorService.deleteActor(id);
        return ResponseEntity.noContent().build();
    }
}