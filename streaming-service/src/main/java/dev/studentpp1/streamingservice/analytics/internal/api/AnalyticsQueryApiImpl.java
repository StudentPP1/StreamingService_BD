package dev.studentpp1.streamingservice.analytics.internal.api;

import dev.studentpp1.streamingservice.analytics.api.AnalyticsQueryApi;
import dev.studentpp1.streamingservice.analytics.api.AnalyticsSummaryView;
import dev.studentpp1.streamingservice.analytics.internal.projection.AnalyticsProjectionStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsQueryApiImpl implements AnalyticsQueryApi {

    private final AnalyticsProjectionStore projectionStore;

    @Override
    public AnalyticsSummaryView getSummary() {
        return projectionStore.view();
    }
}

