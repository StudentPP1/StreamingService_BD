package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.command.CreatePerformanceCommand;
import dev.studentpp1.streamingservice.movies.application.command.DeletePerformanceCommand;
import dev.studentpp1.streamingservice.movies.application.command.PerformanceCommandHandler;
import dev.studentpp1.streamingservice.movies.application.query.GetPerformanceByIdQuery;
import dev.studentpp1.streamingservice.movies.application.query.PerformanceQueryHandler;
import dev.studentpp1.streamingservice.movies.domain.model.Performance;
import dev.studentpp1.streamingservice.movies.presentation.dto.PerformanceDto;
import dev.studentpp1.streamingservice.movies.application.dto.PerformanceCreateRequest;
import dev.studentpp1.streamingservice.movies.presentation.mapper.PerformancePresentationMapper;
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
    private final PerformancePresentationMapper performanceMapper;

    public PerformanceController(PerformanceCommandHandler performanceCommandHandler,
                                 PerformanceQueryHandler performanceQueryHandler,
                                 PerformancePresentationMapper performanceMapper) {
        this.performanceCommandHandler = performanceCommandHandler;
        this.performanceQueryHandler = performanceQueryHandler;
        this.performanceMapper = performanceMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceDto> getPerformanceById(@PathVariable Long id) {
        Performance performance = performanceQueryHandler.handle(new GetPerformanceByIdQuery(id));
        return ResponseEntity.ok(performanceMapper.toDto(performance));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PerformanceDto> createPerformance(
            @RequestBody @Valid PerformanceCreateRequest request) {
        Performance performance = performanceCommandHandler.handle(new CreatePerformanceCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(performanceMapper.toDto(performance));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePerformance(@PathVariable Long id) {
        performanceCommandHandler.handle(new DeletePerformanceCommand(id));
        return ResponseEntity.noContent().build();
    }
}