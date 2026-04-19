package dev.studentpp1.streamingservice.movies.api.query;

import java.util.List;

public interface MoviesQueryApi {
    List<MovieView> findViewsById(List<Long> ids);
}
