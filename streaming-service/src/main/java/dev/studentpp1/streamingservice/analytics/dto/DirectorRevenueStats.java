package dev.studentpp1.streamingservice.analytics.dto;

import java.math.BigDecimal;

public interface DirectorRevenueStats {

    String getDirectorName();

    BigDecimal getTotalRevenue();

    String getPlanNames();

    String getRevenueBreakdownJson();

    Integer getRevenueRank();
}