package dev.studentpp1.streamingservice.movies.domain.exception;

public class PerformanceNotFoundException extends MovieDomainException {
    public PerformanceNotFoundException(Long id) {
        super("Performance with id " + id + " not found");
    }
}
