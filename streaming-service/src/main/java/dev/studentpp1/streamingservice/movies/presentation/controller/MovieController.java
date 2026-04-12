package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.command.movie.CreateMovieCommand;
import dev.studentpp1.streamingservice.movies.application.command.movie.DeleteMovieCommand;
import dev.studentpp1.streamingservice.movies.application.command.MovieCommandHandler;
import dev.studentpp1.streamingservice.movies.application.command.movie.UpdateMovieCommand;
import dev.studentpp1.streamingservice.movies.application.query.movie.GetAllMoviesQuery;
import dev.studentpp1.streamingservice.movies.application.query.movie.GetMovieByIdQuery;
import dev.studentpp1.streamingservice.movies.application.query.movie.GetMovieDetailsQuery;
import dev.studentpp1.streamingservice.movies.application.query.movie.MovieDetailsReadModel;
import dev.studentpp1.streamingservice.movies.application.query.movie.MovieQueryHandler;
import dev.studentpp1.streamingservice.movies.application.query.movie.MovieReadModel;
import dev.studentpp1.streamingservice.movies.presentation.dto.MovieCreateRequest;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieCommandHandler movieCommandHandler;
    private final MovieQueryHandler movieQueryHandler;

    public MovieController(MovieCommandHandler movieCommandHandler,
                           MovieQueryHandler movieQueryHandler) {
        this.movieCommandHandler = movieCommandHandler;
        this.movieQueryHandler = movieQueryHandler;
    }

    @GetMapping
    public ResponseEntity<PageResult<MovieReadModel>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(movieQueryHandler.handle(new GetAllMoviesQuery(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieReadModel> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieQueryHandler.handle(new GetMovieByIdQuery(id)));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<MovieDetailsReadModel> getMovieDetails(@PathVariable Long id) {
        return ResponseEntity.ok(movieQueryHandler.handle(new GetMovieDetailsQuery(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createMovie(@RequestBody @Valid MovieCreateRequest request) {
        movieCommandHandler.handle(new CreateMovieCommand(
                request.title(),
                request.description(),
                request.year(),
                request.rating(),
                request.directorId()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateMovie(@PathVariable Long id,
                                            @RequestBody @Valid MovieCreateRequest request) {
        movieCommandHandler.handle(new UpdateMovieCommand(
                id,
                request.title(),
                request.description(),
                request.year(),
                request.rating(),
                request.directorId(),
                request.version()
        ));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieCommandHandler.handle(new DeleteMovieCommand(id));
        return ResponseEntity.noContent().build();
    }
}
