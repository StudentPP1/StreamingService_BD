package dev.studentpp1.streamingservice.analytics.api;

public record AnalyticsSummaryView(
        long successfulPayments,
        long failedPayments,
        long activatedSubscriptions,
        long failedSubscriptions
) {
}

