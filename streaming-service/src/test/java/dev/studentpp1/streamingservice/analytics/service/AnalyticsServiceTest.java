package dev.studentpp1.streamingservice.analytics.service;

import dev.studentpp1.streamingservice.analytics.dto.DirectorRevenueStats;
import dev.studentpp1.streamingservice.analytics.dto.DirectorRevenueStatsDto;
import dev.studentpp1.streamingservice.analytics.dto.MonthlyPlanStatisticProjection;
import dev.studentpp1.streamingservice.analytics.dto.MonthlyPlanStatisticResponse;
import dev.studentpp1.streamingservice.analytics.mapper.DirectorRevenueMapper;
import dev.studentpp1.streamingservice.analytics.repository.AnalyticsRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private AnalyticsRepository analyticsRepository;

    @Mock
    private DirectorRevenueMapper analyticsMapper;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void getTopDirectorsAggregated_returnsDtoList() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 31, 23, 59);

        DirectorRevenueStats stats1 = new DirectorRevenueStats() {
            @Override public String getDirectorName() { return "Director One"; }
            @Override public BigDecimal getTotalRevenue() { return new BigDecimal("50000"); }
            @Override public String getPlanNames() { return "PREMIUM"; }
            @Override public String getRevenueBreakdownJson() { return "{\"PREMIUM\":50000}"; }
            @Override public Integer getRevenueRank() { return 1; }
        };

        DirectorRevenueStats stats2 = new DirectorRevenueStats() {
            @Override public String getDirectorName() { return "Director Two"; }
            @Override public BigDecimal getTotalRevenue() { return new BigDecimal("30000"); }
            @Override public String getPlanNames() { return "BASIC"; }
            @Override public String getRevenueBreakdownJson() { return "{\"BASIC\":30000}"; }
            @Override public Integer getRevenueRank() { return 2; }
        };

        when(analyticsRepository.findTopDirectorsAggregated(start, end))
                .thenReturn(List.of(stats1, stats2));

        DirectorRevenueStatsDto dto1 = mock(DirectorRevenueStatsDto.class);
        DirectorRevenueStatsDto dto2 = mock(DirectorRevenueStatsDto.class);

        when(analyticsMapper.toDto(stats1)).thenReturn(dto1);
        when(analyticsMapper.toDto(stats2)).thenReturn(dto2);

        List<DirectorRevenueStatsDto> result = analyticsService.getTopDirectorsAggregated(start, end);

        assertThat(result).containsExactly(dto1, dto2);

        verify(analyticsRepository, times(1)).findTopDirectorsAggregated(start, end);
        verify(analyticsMapper, times(1)).toDto(stats1);
        verify(analyticsMapper, times(1)).toDto(stats2);
        verifyNoMoreInteractions(analyticsRepository, analyticsMapper);
    }

    @Test
    void getTopDirectorsAggregated_returnsEmptyList_whenNoData() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 31, 23, 59);

        when(analyticsRepository.findTopDirectorsAggregated(start, end))
                .thenReturn(List.of());

        List<DirectorRevenueStatsDto> result = analyticsService.getTopDirectorsAggregated(start, end);

        assertThat(result).isEmpty();

        verify(analyticsRepository, times(1)).findTopDirectorsAggregated(start, end);
        verifyNoInteractions(analyticsMapper);
    }

    @Test
    void getMonthlyPlanStatistics_returnsMonthlyStats() {
        MonthlyPlanStatisticProjection projection1 = new MonthlyPlanStatisticProjection() {
            @Override public LocalDate getCurrentMonth() { return LocalDate.of(2025, 12, 1); }
            @Override public String getPlanName() { return "PREMIUM"; }
            @Override public Long getUniqueUsers() { return 100L; }
            @Override public Long getPaymentCount() { return 150L; }
            @Override public BigDecimal getTotalPlanAmount() { return new BigDecimal("3000000"); }
            @Override public BigDecimal getMonthSum() { return new BigDecimal("5000000"); }
            @Override public BigDecimal getPercentInTotalSum() { return new BigDecimal("60.00"); }
        };

        MonthlyPlanStatisticProjection projection2 = new MonthlyPlanStatisticProjection() {
            @Override public LocalDate getCurrentMonth() { return LocalDate.of(2025, 12, 1); }
            @Override public String getPlanName() { return "BASIC"; }
            @Override public Long getUniqueUsers() { return 50L; }
            @Override public Long getPaymentCount() { return 50L; }
            @Override public BigDecimal getTotalPlanAmount() { return new BigDecimal("500000"); }
            @Override public BigDecimal getMonthSum() { return new BigDecimal("5000000"); }
            @Override public BigDecimal getPercentInTotalSum() { return new BigDecimal("10.00"); }
        };

        when(analyticsRepository.findMonthlyPlanStatistics())
                .thenReturn(List.of(projection1, projection2));

        List<MonthlyPlanStatisticResponse> result = analyticsService.getMonthlyPlanStatistics();

        assertThat(result).hasSize(2);

        MonthlyPlanStatisticResponse r1 = result.get(0);
        assertThat(r1.currentMonth()).isEqualTo(LocalDate.of(2025, 12, 1));
        assertThat(r1.planName()).isEqualTo("PREMIUM");
        assertThat(r1.uniqueUsers()).isEqualTo(100L);
        assertThat(r1.paymentCount()).isEqualTo(150L);
        assertThat(r1.totalPlanAmount()).isEqualByComparingTo(new BigDecimal("3000000"));
        assertThat(r1.monthSum()).isEqualByComparingTo(new BigDecimal("5000000"));
        assertThat(r1.percentInTotalSum()).isEqualByComparingTo(new BigDecimal("60.00"));

        MonthlyPlanStatisticResponse r2 = result.get(1);
        assertThat(r2.planName()).isEqualTo("BASIC");
        assertThat(r2.uniqueUsers()).isEqualTo(50L);

        verify(analyticsRepository, times(1)).findMonthlyPlanStatistics();
        verifyNoMoreInteractions(analyticsRepository);
        verifyNoInteractions(analyticsMapper);
    }

    @Test
    void getMonthlyPlanStatistics_returnsEmptyList_whenNoData() {
        when(analyticsRepository.findMonthlyPlanStatistics())
                .thenReturn(List.of());

        List<MonthlyPlanStatisticResponse> result = analyticsService.getMonthlyPlanStatistics();

        assertThat(result).isEmpty();

        verify(analyticsRepository, times(1)).findMonthlyPlanStatistics();
        verifyNoInteractions(analyticsMapper);
    }
}
