package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.usecase.DirectorService;
import dev.studentpp1.streamingservice.movies.application.usecase.DirectorService.DirectorWithMovies;
import dev.studentpp1.streamingservice.movies.domain.model.Director;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.DirectorDetailDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.DirectorDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.request.DirectorCreateRequest;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.MovieDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.response.PageResponse;
import dev.studentpp1.streamingservice.movies.presentation.mapper.DirectorPresentationMapper;
import dev.studentpp1.streamingservice.movies.presentation.mapper.MoviePresentationMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directors")
public class DirectorController {

    private final DirectorService directorService;
    private final DirectorPresentationMapper directorMapper;
    private final MoviePresentationMapper movieMapper;

    public DirectorController(DirectorService directorService,
                              DirectorPresentationMapper directorMapper,
                              MoviePresentationMapper movieMapper) {
        this.directorService = directorService;
        this.directorMapper = directorMapper;
        this.movieMapper = movieMapper;
    }

    @GetMapping
    public ResponseEntity<PageResponse<DirectorDto>> getAllDirectors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<Director> result = directorService.getAllDirectors(page, size);
        PageResponse<DirectorDto> response = new PageResponse<>(
                result.content().stream().map(directorMapper::toDto).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectorDto> getDirectorById(@PathVariable Long id) {
        Director director = directorService.getDirectorById(id);
        return ResponseEntity.ok(directorMapper.toDto(director));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<DirectorDetailDto> getDirectorDetails(@PathVariable Long id) {
        DirectorWithMovies result = directorService.getDirectorDetails(id);
        List<MovieDto> movieDtos = movieMapper.toDtoList(result.movies());
        return ResponseEntity.ok(directorMapper.toDetailDto(result.director(), movieDtos));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DirectorDto> createDirector(@RequestBody @Valid DirectorCreateRequest request) {
        Director director = directorService.createDirector(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(directorMapper.toDto(director));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DirectorDto> updateDirector(@PathVariable Long id,
                                                      @RequestBody @Valid DirectorCreateRequest request) {
        Director director = directorService.updateDirector(id, request);
        return ResponseEntity.ok(directorMapper.toDto(director));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDirector(@PathVariable Long id) {
        directorService.deleteDirector(id);
        return ResponseEntity.noContent().build();
    }
}