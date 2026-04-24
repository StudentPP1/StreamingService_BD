package dev.studentpp1.streamingservice.analytics.internal.impl;

import dev.studentpp1.streamingservice.analytics.api.AnalyticsQueryApi;
import dev.studentpp1.streamingservice.analytics.api.AnalyticsSummaryView;
import dev.studentpp1.streamingservice.analytics.internal.data.AnalyticsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsQueryApiImpl implements AnalyticsQueryApi {

    private final AnalyticsData projectionStore;

    @Override
    public AnalyticsSummaryView getSummary() {
        return projectionStore.view();
    }
}

