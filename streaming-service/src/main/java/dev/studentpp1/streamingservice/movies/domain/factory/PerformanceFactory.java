package dev.studentpp1.streamingservice.movies.domain.factory;

import dev.studentpp1.streamingservice.movies.domain.exception.ActorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.MovieNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.model.Performance;
import dev.studentpp1.streamingservice.movies.domain.repository.ActorRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;

public class PerformanceFactory {
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;

    public PerformanceFactory(MovieRepository movieRepository,
                              ActorRepository actorRepository) {
        this.movieRepository = movieRepository;
        this.actorRepository = actorRepository;
    }

    public Performance create(Long movieId, Long actorId,
                              String characterName, String description) {
        if (!movieRepository.existsById(movieId))
            throw new MovieNotFoundException(movieId);
        if (!actorRepository.existsById(actorId))
            throw new ActorNotFoundException(actorId);

        return Performance.create(movieId, actorId, characterName, description);
    }
}