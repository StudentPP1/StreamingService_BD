package dev.studentpp1.streamingservice.movies.domain.exception;

public class OptimisticLockingException extends RuntimeException {
    public OptimisticLockingException(String message) {
        super(message);
    }
}
