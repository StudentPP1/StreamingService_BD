package dev.studentpp1.streamingservice.subscription.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MoviesNotInPlanException extends RuntimeException {
    public MoviesNotInPlanException() {
        super("None of the provided movies were found in the plan");
    }
}

