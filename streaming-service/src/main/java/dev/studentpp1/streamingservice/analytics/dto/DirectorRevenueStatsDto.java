package dev.studentpp1.streamingservice.analytics.dto;

import java.math.BigDecimal;
import java.util.Map;

public record DirectorRevenueStatsDto(
    String directorName,
    BigDecimal totalRevenue,
    String planNames,
    Map<String, BigDecimal> revenueBreakdown,
    Integer revenueRank
) {

}