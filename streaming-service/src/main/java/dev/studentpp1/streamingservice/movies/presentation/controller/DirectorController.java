package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.command.CreateDirectorCommand;
import dev.studentpp1.streamingservice.movies.application.command.DeleteDirectorCommand;
import dev.studentpp1.streamingservice.movies.application.command.DirectorCommandHandler;
import dev.studentpp1.streamingservice.movies.application.command.UpdateDirectorCommand;
import dev.studentpp1.streamingservice.movies.application.query.DirectorQueryHandler;
import dev.studentpp1.streamingservice.movies.application.query.GetAllDirectorsQuery;
import dev.studentpp1.streamingservice.movies.application.query.GetDirectorByIdQuery;
import dev.studentpp1.streamingservice.movies.application.query.GetDirectorDetailsQuery;
import dev.studentpp1.streamingservice.movies.domain.model.Director;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.presentation.dto.DirectorDetailDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.DirectorDto;
import dev.studentpp1.streamingservice.movies.application.dto.DirectorCreateRequest;
import dev.studentpp1.streamingservice.movies.presentation.dto.MovieDto;
import dev.studentpp1.streamingservice.movies.presentation.dto.PageResponse;
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

    private final DirectorCommandHandler directorCommandHandler;
    private final DirectorQueryHandler directorQueryHandler;
    private final DirectorPresentationMapper directorMapper;
    private final MoviePresentationMapper movieMapper;

    public DirectorController(DirectorCommandHandler directorCommandHandler,
                              DirectorQueryHandler directorQueryHandler,
                              DirectorPresentationMapper directorMapper,
                              MoviePresentationMapper movieMapper) {
        this.directorCommandHandler = directorCommandHandler;
        this.directorQueryHandler = directorQueryHandler;
        this.directorMapper = directorMapper;
        this.movieMapper = movieMapper;
    }

    @GetMapping
    public ResponseEntity<PageResponse<DirectorDto>> getAllDirectors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<Director> result = directorQueryHandler.handle(new GetAllDirectorsQuery(page, size));
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
        Director director = directorQueryHandler.handle(new GetDirectorByIdQuery(id));
        return ResponseEntity.ok(directorMapper.toDto(director));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<DirectorDetailDto> getDirectorDetails(@PathVariable Long id) {
        var result = directorQueryHandler.handle(new GetDirectorDetailsQuery(id));
        List<MovieDto> movieDtos = movieMapper.toDtoList(result.movies());
        return ResponseEntity.ok(directorMapper.toDetailDto(result.director(), movieDtos));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DirectorDto> createDirector(@RequestBody @Valid DirectorCreateRequest request) {
        Director director = directorCommandHandler.handle(new CreateDirectorCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(directorMapper.toDto(director));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DirectorDto> updateDirector(@PathVariable Long id,
                                                      @RequestBody @Valid DirectorCreateRequest request) {
        Director director = directorCommandHandler.handle(new UpdateDirectorCommand(id, request));
        return ResponseEntity.ok(directorMapper.toDto(director));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDirector(@PathVariable Long id) {
        directorCommandHandler.handle(new DeleteDirectorCommand(id));
        return ResponseEntity.noContent().build();
    }
}