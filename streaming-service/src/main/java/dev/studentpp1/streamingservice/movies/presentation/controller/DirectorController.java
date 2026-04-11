package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.cqs.MoviesCqs.*;
import dev.studentpp1.streamingservice.movies.application.cqs.MoviesCommandHandler;
import dev.studentpp1.streamingservice.movies.application.cqs.MoviesQueryHandler;
import dev.studentpp1.streamingservice.movies.presentation.dto.request.DirectorCreateRequest;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.DirectorDetailDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.DirectorDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final MoviesCommandHandler movieCommandHandler;
    private final MoviesQueryHandler movieQueryHandler;

    @GetMapping
    public ResponseEntity<PageResponse<DirectorDto>> getAllDirectors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(movieQueryHandler.handle(new GetAllDirectorsQuery(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectorDto> getDirectorById(@PathVariable Long id) {
        return ResponseEntity.ok(movieQueryHandler.handle(new GetDirectorByIdQuery(id)));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<DirectorDetailDto> getDirectorDetails(@PathVariable Long id) {
        return ResponseEntity.ok(movieQueryHandler.handle(new GetDirectorDetailsQuery(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DirectorDto> createDirector(@RequestBody @Valid DirectorCreateRequest request) {
        Long id = movieCommandHandler.handle(new CreateDirectorCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(movieQueryHandler.handle(new GetDirectorByIdQuery(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DirectorDto> updateDirector(@PathVariable Long id,
                                                      @RequestBody @Valid DirectorCreateRequest request) {
        movieCommandHandler.handle(new UpdateDirectorCommand(id, request));
        return ResponseEntity.ok(movieQueryHandler.handle(new GetDirectorByIdQuery(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDirector(@PathVariable Long id) {
        movieCommandHandler.handle(new DeleteDirectorCommand(id));
        return ResponseEntity.noContent().build();
    }
}