package dev.studentpp1.streamingservice.analytics.internal.impl;

import dev.studentpp1.streamingservice.analytics.api.AnalyticsQueryApi;
import dev.studentpp1.streamingservice.analytics.api.AnalyticsSummaryView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsQueryApiImpl implements AnalyticsQueryApi {

    private final AnalyticsData data;

    @Override
    public AnalyticsSummaryView getSummary() {
        return data.view();
    }
}

