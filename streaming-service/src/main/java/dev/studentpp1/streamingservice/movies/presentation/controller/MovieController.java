package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.command.CreateMovieCommand;
import dev.studentpp1.streamingservice.movies.application.command.DeleteMovieCommand;
import dev.studentpp1.streamingservice.movies.application.command.MovieCommandHandler;
import dev.studentpp1.streamingservice.movies.application.command.UpdateMovieCommand;
import dev.studentpp1.streamingservice.movies.application.query.GetAllMoviesQuery;
import dev.studentpp1.streamingservice.movies.application.query.GetMovieByIdQuery;
import dev.studentpp1.streamingservice.movies.application.query.GetMovieDetailsQuery;
import dev.studentpp1.streamingservice.movies.application.query.MovieQueryHandler;
import dev.studentpp1.streamingservice.movies.application.usecase.MovieService;
import dev.studentpp1.streamingservice.movies.application.usecase.MovieService.MovieDetails;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.presentation.dto.MovieDetailDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.MovieDto;
import dev.studentpp1.streamingservice.movies.application.dto.MovieCreateRequest;
import dev.studentpp1.streamingservice.movies.presentation.dto.PageResponse;
import dev.studentpp1.streamingservice.movies.presentation.mapper.MoviePresentationMapper;
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
    private final MoviePresentationMapper movieMapper;

    public MovieController(MovieCommandHandler movieCommandHandler,
                           MovieQueryHandler movieQueryHandler,
                           MoviePresentationMapper movieMapper) {
        this.movieCommandHandler = movieCommandHandler;
        this.movieQueryHandler = movieQueryHandler;
        this.movieMapper = movieMapper;
    }

    @GetMapping
    public ResponseEntity<PageResponse<MovieDto>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<Movie> result = movieQueryHandler.handle(new GetAllMoviesQuery(page, size));
        PageResponse<MovieDto> response = new PageResponse<>(
                result.content().stream().map(movieMapper::toDto).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id) {
        Movie movie = movieQueryHandler.handle(new GetMovieByIdQuery(id));
        return ResponseEntity.ok(movieMapper.toDto(movie));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<MovieDetailDto> getMovieDetails(@PathVariable Long id) {
        var details = movieQueryHandler.handle(new GetMovieDetailsQuery(id));
        return ResponseEntity.ok(movieMapper.toDetailDto(details));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDto> createMovie(@RequestBody @Valid MovieCreateRequest request) {
        Movie movie = movieCommandHandler.handle(new CreateMovieCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(movieMapper.toDto(movie));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable Long id,
                                                @RequestBody @Valid MovieCreateRequest request) {
        Movie movie = movieCommandHandler.handle(new UpdateMovieCommand(id, request));
        return ResponseEntity.ok(movieMapper.toDto(movie));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieCommandHandler.handle(new DeleteMovieCommand(id));
        return ResponseEntity.noContent().build();
    }
}