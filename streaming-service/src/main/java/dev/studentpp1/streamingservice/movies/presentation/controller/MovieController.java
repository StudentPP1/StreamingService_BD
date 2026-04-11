package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.cqs.MoviesCqs.*;
import dev.studentpp1.streamingservice.movies.application.cqs.MoviesCommandHandler;
import dev.studentpp1.streamingservice.movies.application.cqs.MoviesQueryHandler;
import dev.studentpp1.streamingservice.movies.presentation.dto.request.MovieCreateRequest;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.MovieDetailDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.MovieDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MoviesCommandHandler movieCommandHandler;
    private final MoviesQueryHandler movieQueryHandler;

    @GetMapping
    public ResponseEntity<PageResponse<MovieDto>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(movieQueryHandler.handle(new GetAllMoviesQuery(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieQueryHandler.handle(new GetMovieByIdQuery(id)));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<MovieDetailDto> getMovieDetails(@PathVariable Long id) {
        return ResponseEntity.ok(movieQueryHandler.handle(new GetMovieDetailsQuery(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDto> createMovie(@RequestBody @Valid MovieCreateRequest request) {
        Long id = movieCommandHandler.handle(new CreateMovieCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(movieQueryHandler.handle(new GetMovieByIdQuery(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable Long id,
                                                @RequestBody @Valid MovieCreateRequest request) {
        movieCommandHandler.handle(new UpdateMovieCommand(id, request));
        return ResponseEntity.ok(movieQueryHandler.handle(new GetMovieByIdQuery(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieCommandHandler.handle(new DeleteMovieCommand(id));
        return ResponseEntity.noContent().build();
    }
}