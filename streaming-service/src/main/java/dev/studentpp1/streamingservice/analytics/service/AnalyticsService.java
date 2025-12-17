package dev.studentpp1.streamingservice.analytics.service;

import dev.studentpp1.streamingservice.analytics.dto.DirectorRevenueStatsDto;
import dev.studentpp1.streamingservice.analytics.mapper.DirectorRevenueMapper;
import dev.studentpp1.streamingservice.analytics.dto.MonthlyPlanStatisticResponse;
import dev.studentpp1.streamingservice.analytics.repository.AnalyticsRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final DirectorRevenueMapper analyticsMapper;

    public List<DirectorRevenueStatsDto> getTopDirectorsAggregated(LocalDateTime start,
        LocalDateTime end) {

        return analyticsRepository.findTopDirectorsAggregated(start, end)
            .stream()
            .map(analyticsMapper::toDto)
            .toList();
    }

    public List<MonthlyPlanStatisticResponse> getMonthlyPlanStatistics() {
        return analyticsRepository.findMonthlyPlanStatistics().stream()
            .map(p -> new MonthlyPlanStatisticResponse(
                    p.getCurrentMonth(),
                    p.getPlanName(),
                    p.getUniqueUsers(),
                    p.getPaymentCount(),
                    p.getTotalPlanAmount(),
                    p.getMonthSum(),
                    p.getPercentInTotalSum()
                )
            ).toList();
    }
}