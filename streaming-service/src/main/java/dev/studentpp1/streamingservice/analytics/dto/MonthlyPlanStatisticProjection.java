package dev.studentpp1.streamingservice.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface MonthlyPlanStatisticProjection {
    LocalDate getCurrentMonth();
    String getPlanName();
    Long getUniqueUsers();
    Long getPaymentCount();
    BigDecimal getTotalPlanAmount();
    BigDecimal getMonthSum();
    BigDecimal getPercentInTotalSum();
}
