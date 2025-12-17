package dev.studentpp1.streamingservice.analytics.service;

import dev.studentpp1.streamingservice.analytics.dto.ActorAnalyticsStats; // Імпорт
import dev.studentpp1.streamingservice.analytics.dto.DirectorRevenueStats;
import dev.studentpp1.streamingservice.analytics.dto.MonthlyPlanStatisticResponse;
import dev.studentpp1.streamingservice.analytics.repository.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public List<DirectorRevenueStats> getTopDirectorsByRevenue() {
        return analyticsRepository.findTopDirectorsByRevenue();
    }

    public List<ActorAnalyticsStats> getActorAnalytics() {
        return analyticsRepository.findActorAnalytics();
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