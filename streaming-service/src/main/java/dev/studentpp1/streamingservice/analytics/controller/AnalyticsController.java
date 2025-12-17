package dev.studentpp1.streamingservice.analytics.controller;

import dev.studentpp1.streamingservice.analytics.dto.ActorAnalyticsStats; // Імпорт
import dev.studentpp1.streamingservice.analytics.dto.DirectorRevenueStats;
import dev.studentpp1.streamingservice.analytics.dto.DirectorRevenueStatsDto;
import dev.studentpp1.streamingservice.analytics.dto.MonthlyPlanStatisticResponse;
import dev.studentpp1.streamingservice.analytics.service.AnalyticsService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/directors-roi")
    public ResponseEntity<List<DirectorRevenueStatsDto>> getDirectorsRoi(
        @RequestParam(required = false, defaultValue = "2025-01-01T00:00:00") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(analyticsService.getTopDirectorsAggregated(from, to));
    }

    @GetMapping("/actors-rating")
    public ResponseEntity<List<ActorAnalyticsStats>> getActorsRating() {
        return ResponseEntity.ok(analyticsService.getActorAnalytics());
    }

    @GetMapping("/monthly-plans")
    public ResponseEntity<List<MonthlyPlanStatisticResponse>> getMonthlyPlansAnalytics() {
        return ResponseEntity.ok(analyticsService.getMonthlyPlanStatistics());
    }
}