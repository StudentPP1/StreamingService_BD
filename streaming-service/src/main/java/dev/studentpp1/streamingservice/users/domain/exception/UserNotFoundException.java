package dev.studentpp1.streamingservice.users.domain.exception;

public class UserNotFoundException extends UserDomainException {
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found");
    }
    public UserNotFoundException(String email) {
        super("User with email " + email + " not found");
    }
}