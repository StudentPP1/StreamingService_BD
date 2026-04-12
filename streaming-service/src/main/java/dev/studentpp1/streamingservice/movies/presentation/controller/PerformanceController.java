package dev.studentpp1.streamingservice.movies.presentation.controller;

import dev.studentpp1.streamingservice.movies.application.usecase.PerformanceService;
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

    private final PerformanceService performanceService;
    private final PerformancePresentationMapper performanceMapper;

    public PerformanceController(PerformanceService performanceService,
                                 PerformancePresentationMapper performanceMapper) {
        this.performanceService = performanceService;
        this.performanceMapper = performanceMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceDto> getPerformanceById(@PathVariable Long id) {
        Performance performance = performanceService.getPerformanceById(id);
        return ResponseEntity.ok(performanceMapper.toDto(performance));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PerformanceDto> createPerformance(
            @RequestBody @Valid PerformanceCreateRequest request) {
        Performance performance = performanceService.createPerformance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(performanceMapper.toDto(performance));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePerformance(@PathVariable Long id) {
        performanceService.deletePerformance(id);
        return ResponseEntity.noContent().build();
    }
}