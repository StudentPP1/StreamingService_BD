package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.command.director.CreateDirectorCommand;
import dev.studentpp1.streamingservice.movies.application.command.director.DeleteDirectorCommand;
import dev.studentpp1.streamingservice.movies.application.command.director.DirectorCommandHandler;
import dev.studentpp1.streamingservice.movies.application.command.director.DirectorCreateRequest;
import dev.studentpp1.streamingservice.movies.application.command.director.UpdateDirectorCommand;
import dev.studentpp1.streamingservice.movies.application.query.director.DirectorDetailsReadModel;
import dev.studentpp1.streamingservice.movies.application.query.director.DirectorQueryHandler;
import dev.studentpp1.streamingservice.movies.application.query.director.DirectorReadModel;
import dev.studentpp1.streamingservice.movies.application.query.director.GetAllDirectorsQuery;
import dev.studentpp1.streamingservice.movies.application.query.director.GetDirectorByIdQuery;
import dev.studentpp1.streamingservice.movies.application.query.director.GetDirectorDetailsQuery;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/directors")
public class DirectorController {

    private final DirectorCommandHandler directorCommandHandler;
    private final DirectorQueryHandler directorQueryHandler;

    public DirectorController(DirectorCommandHandler directorCommandHandler,
                              DirectorQueryHandler directorQueryHandler) {
        this.directorCommandHandler = directorCommandHandler;
        this.directorQueryHandler = directorQueryHandler;
    }

    @GetMapping
    public ResponseEntity<PageResult<DirectorReadModel>> getAllDirectors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(directorQueryHandler.handle(new GetAllDirectorsQuery(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectorReadModel> getDirectorById(@PathVariable Long id) {
        return ResponseEntity.ok(directorQueryHandler.handle(new GetDirectorByIdQuery(id)));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<DirectorDetailsReadModel> getDirectorDetails(@PathVariable Long id) {
        return ResponseEntity.ok(directorQueryHandler.handle(new GetDirectorDetailsQuery(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createDirector(@RequestBody @Valid DirectorCreateRequest request) {
        directorCommandHandler.handle(new CreateDirectorCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateDirector(@PathVariable Long id,
                                               @RequestBody @Valid DirectorCreateRequest request) {
        directorCommandHandler.handle(new UpdateDirectorCommand(id, request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDirector(@PathVariable Long id) {
        directorCommandHandler.handle(new DeleteDirectorCommand(id));
        return ResponseEntity.noContent().build();
    }
}
