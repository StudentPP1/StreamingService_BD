package dev.studentpp1.streamingservice.subscription.exception;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MoviesNotFoundException extends RuntimeException {

    public MoviesNotFoundException(List<Long> missingIds) {
        super("Movies not found with ids: " + missingIds);
    }
}
