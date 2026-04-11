package dev.studentpp1.streamingservice.movies.domain.factory;

import dev.studentpp1.streamingservice.movies.domain.exception.DirectorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.movies.domain.repository.DirectorRepository;

import java.math.BigDecimal;
public class MovieFactory {
    private final DirectorRepository directorRepository;

    public MovieFactory(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    public Movie create(String title, String description,
                        int year, BigDecimal rating, Long directorId) {
        if (!directorRepository.existsById(directorId)) {
            throw new DirectorNotFoundException(directorId);
        }

        return Movie.create(title, description, year, rating, directorId);
    }
}