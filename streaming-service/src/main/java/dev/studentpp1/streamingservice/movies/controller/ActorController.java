package dev.studentpp1.streamingservice.movies.controller;

import dev.studentpp1.streamingservice.movies.dto.ActorDetailDto;
import dev.studentpp1.streamingservice.movies.dto.ActorDto;
import dev.studentpp1.streamingservice.movies.dto.ActorRequest;
import dev.studentpp1.streamingservice.movies.service.ActorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ActorDetailDto> getActorDetails(@PathVariable Long id) {
        return ResponseEntity.ok(actorService.getActorDetails(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorDto> getActorById(@PathVariable Long id) {
        return ResponseEntity.ok(actorService.getActorById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActorDto> createActor(@RequestBody @Valid ActorRequest request) {
        return new ResponseEntity<>(actorService.createActor(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActorDto> updateActor(@PathVariable Long id, @RequestBody @Valid ActorRequest request) {
        return ResponseEntity.ok(actorService.updateActor(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id) {
        actorService.deleteActor(id);
        return ResponseEntity.noContent().build();
    }
}