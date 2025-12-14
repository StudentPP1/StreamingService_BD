package dev.studentpp1.streamingservice.analytics.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;

@JsonSerialize(as = DirectorRevenueStats.class)
public interface DirectorRevenueStats {
    String getDirectorName();
    String getPlanName();
    BigDecimal getTotalGeneratedRevenue();
    Integer getRevenueRank();
}