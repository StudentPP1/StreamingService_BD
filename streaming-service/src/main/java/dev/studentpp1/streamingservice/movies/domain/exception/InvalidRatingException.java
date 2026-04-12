package dev.studentpp1.streamingservice.movies.domain.exception;

public class InvalidRatingException extends MovieDomainException {
    public InvalidRatingException(double rating) {
        super("Rating must be between 0.0 and 10.0, got: " + rating);
    }

    public InvalidRatingException(Double rating) {
        super("Rating must be between 0.0 and 10.0, got: " + rating);
    }
}
