package dev.studentpp1.streamingservice.analytics.presentation.controller;

import dev.studentpp1.streamingservice.analytics.api.AnalyticsQueryApi;
import dev.studentpp1.streamingservice.analytics.api.AnalyticsSummaryView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsQueryApi analyticsQueryApi;

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryView> summary() {
        return ResponseEntity.ok(analyticsQueryApi.getSummary());
    }
}

