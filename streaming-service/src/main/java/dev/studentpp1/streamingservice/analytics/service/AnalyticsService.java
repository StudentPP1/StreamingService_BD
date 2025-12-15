package dev.studentpp1.streamingservice.analytics.service;

import dev.studentpp1.streamingservice.analytics.dto.DirectorRevenueStatsDto;
import dev.studentpp1.streamingservice.analytics.mapper.DirectorRevenueMapper;
import dev.studentpp1.streamingservice.analytics.repository.AnalyticsRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final DirectorRevenueMapper analyticsMapper;

    @Transactional(readOnly = true)
    public List<DirectorRevenueStatsDto> getTopDirectorsAggregated(LocalDateTime start,
        LocalDateTime end) {

        return analyticsRepository.findTopDirectorsAggregated(start, end)
            .stream()
            .map(analyticsMapper::toDto)
            .toList();
    }
}