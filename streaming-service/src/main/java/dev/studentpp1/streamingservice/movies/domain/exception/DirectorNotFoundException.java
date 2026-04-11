package dev.studentpp1.streamingservice.movies.domain.exception;

public class DirectorNotFoundException extends MovieDomainException {
    public DirectorNotFoundException(Long id) {
        super("Director with id " + id + " not found");
    }
}
