package dev.studentpp1.streamingservice.movies.domain.exception;

public class ActorNotFoundException extends MovieDomainException {
    public ActorNotFoundException(Long id) {
        super("Actor with id " + id + " not found");
    }
}