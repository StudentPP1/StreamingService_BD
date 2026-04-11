package dev.studentpp1.streamingservice.movies.application.usecase;

import dev.studentpp1.streamingservice.movies.domain.exception.DirectorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.factory.DirectorFactory;
import dev.studentpp1.streamingservice.movies.domain.model.Director;
import dev.studentpp1.streamingservice.movies.domain.model.Movie;
import dev.studentpp1.streamingservice.common.dto.PageResult;
import dev.studentpp1.streamingservice.movies.domain.repository.DirectorRepository;
import dev.studentpp1.streamingservice.movies.domain.repository.MovieRepository;
import dev.studentpp1.streamingservice.movies.presentation.dto.request.DirectorCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorRepository directorRepository;
    private final MovieRepository movieRepository;
    private final DirectorFactory directorFactory;

    public DirectorService(DirectorRepository directorRepository,
                           MovieRepository movieRepository,
                           DirectorFactory directorFactory) {
        this.directorRepository = directorRepository;
        this.movieRepository = movieRepository;
        this.directorFactory = directorFactory;
    }

    @Transactional(readOnly = true)
    public Director getDirectorById(Long id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new DirectorNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public DirectorWithMovies getDirectorDetails(Long id) {
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new DirectorNotFoundException(id));
        List<Movie> movies = movieRepository.findAllByDirectorEntityId(id);
        return new DirectorWithMovies(director, movies);
    }

    @Transactional(readOnly = true)
    public PageResult<Director> getAllDirectors(int page, int size) {
        return directorRepository.findAll(page, size);
    }

    @Transactional
    public Director createDirector(DirectorCreateRequest request) {
        Director director = directorFactory.create(
                request.name(),
                request.surname(),
                request.biography()
        );
        return directorRepository.save(director);
    }

    @Transactional
    public Director updateDirector(Long id, DirectorCreateRequest request) {
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new DirectorNotFoundException(id));

        director.update(request.name(), request.surname(), request.biography());

        return directorRepository.save(director);
    }

    @Transactional
    public void deleteDirector(Long id) {
        if (!directorRepository.existsById(id)) {
            throw new DirectorNotFoundException(id);
        }
        directorRepository.deleteById(id);
    }

    public record DirectorWithMovies(Director director, List<Movie> movies) {}
}