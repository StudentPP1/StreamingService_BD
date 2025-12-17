package dev.studentpp1.streamingservice.subscription.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFamilyMemberException extends RuntimeException {

    public InvalidFamilyMemberException() {
        super("Cannot add yourself as a family member");
    }
}
