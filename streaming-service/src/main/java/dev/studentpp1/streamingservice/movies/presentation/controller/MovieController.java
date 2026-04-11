package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.usecase.MovieService;
import dev.studentpp1.streamingservice.movies.application.usecase.MovieService.MovieDetails;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.MovieDetailDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.MovieDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.request.MovieCreateRequest;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.PageResponse;
import dev.studentpp1.streamingservice.movies.presentation.mapper.MoviePresentationMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;
    private final MoviePresentationMapper movieMapper;

    public MovieController(MovieService movieService,
                           MoviePresentationMapper movieMapper) {
        this.movieService = movieService;
        this.movieMapper = movieMapper;
    }

    @GetMapping
    public ResponseEntity<PageResponse<MovieDto>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<Movie> result = movieService.getAllMovies(page, size);
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
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movieMapper.toDto(movie));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<MovieDetailDto> getMovieDetails(@PathVariable Long id) {
        MovieDetails details = movieService.getMovieDetails(id);
        return ResponseEntity.ok(movieMapper.toDetailDto(details));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDto> createMovie(@RequestBody @Valid MovieCreateRequest request) {
        Movie movie = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(movieMapper.toDto(movie));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable Long id,
                                                @RequestBody @Valid MovieCreateRequest request) {
        Movie movie = movieService.updateMovie(id, request);
        return ResponseEntity.ok(movieMapper.toDto(movie));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}