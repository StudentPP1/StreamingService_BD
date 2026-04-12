package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.command.performance.CreatePerformanceCommand;
import dev.studentpp1.streamingservice.movies.application.command.performance.DeletePerformanceCommand;
import dev.studentpp1.streamingservice.movies.application.command.PerformanceCommandHandler;
import dev.studentpp1.streamingservice.movies.application.query.performance.GetPerformanceByIdQuery;
import dev.studentpp1.streamingservice.movies.application.query.performance.PerformanceQueryHandler;
import dev.studentpp1.streamingservice.movies.application.query.performance.PerformanceReadModel;
import dev.studentpp1.streamingservice.movies.presentation.dto.PerformanceCreateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/performances")
public class PerformanceController {

    private final PerformanceCommandHandler performanceCommandHandler;
    private final PerformanceQueryHandler performanceQueryHandler;

    public PerformanceController(PerformanceCommandHandler performanceCommandHandler,
                                 PerformanceQueryHandler performanceQueryHandler) {
        this.performanceCommandHandler = performanceCommandHandler;
        this.performanceQueryHandler = performanceQueryHandler;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceReadModel> getPerformanceById(@PathVariable Long id) {
        return ResponseEntity.ok(performanceQueryHandler.handle(new GetPerformanceByIdQuery(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createPerformance(@RequestBody @Valid PerformanceCreateRequest request) {
        performanceCommandHandler.handle(new CreatePerformanceCommand(
                request.characterName(),
                request.description(),
                request.actorId(),
                request.movieId()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePerformance(@PathVariable Long id) {
        performanceCommandHandler.handle(new DeletePerformanceCommand(id));
        return ResponseEntity.noContent().build();
    }
}
