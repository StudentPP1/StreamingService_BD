package dev.studentpp1.streamingservice.users.domain.exception;

public class UserAlreadyExistsException extends UserDomainException {
    public UserAlreadyExistsException(String email) {
        super("User with email " + email + " already exists");
    }
}