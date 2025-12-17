package dev.studentpp1.streamingservice.analytics.dto;

import java.math.BigDecimal;

public interface ActorAnalyticsStats {
    String getFullName();
    Integer getTotalMovies();
    Integer getDistinctDirectors();
    BigDecimal getActorRating();
    Integer getRankInSystem();
    String getPerformanceStatus();
}