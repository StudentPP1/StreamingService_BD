package dev.studentpp1.streamingservice.movies.infrastructure.adapter;

import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.domain.port.MovieQueryPort;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieQueryPortAdapter implements MovieQueryPort {

    private final MovieRepository movieRepository;

    @Override
    public List<Movie> findAllById(List<Long> ids) {
        return movieRepository.findAllById(ids);
    }
}
