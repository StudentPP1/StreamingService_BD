package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.cqs.MoviesCqs.*;
import dev.studentpp1.streamingservice.movies.application.cqs.MoviesCommandHandler;
import dev.studentpp1.streamingservice.movies.application.cqs.MoviesQueryHandler;
import dev.studentpp1.streamingservice.movies.presentation.dto.request.PerformanceCreateRequest;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.PerformanceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final MoviesCommandHandler movieCommandHandler;
    private final MoviesQueryHandler movieQueryHandler;

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceDto> getPerformanceById(@PathVariable Long id) {
        return ResponseEntity.ok(movieQueryHandler.handle(new GetPerformanceByIdQuery(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PerformanceDto> createPerformance(
            @RequestBody @Valid PerformanceCreateRequest request) {
        Long id = movieCommandHandler.handle(new CreatePerformanceCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(movieQueryHandler.handle(new GetPerformanceByIdQuery(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePerformance(@PathVariable Long id) {
        movieCommandHandler.handle(new DeletePerformanceCommand(id));
        return ResponseEntity.noContent().build();
    }
}