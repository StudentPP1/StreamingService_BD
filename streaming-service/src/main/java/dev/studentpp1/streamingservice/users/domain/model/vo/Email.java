package dev.studentpp1.streamingservice.users.domain.model.vo;

import dev.studentpp1.streamingservice.users.domain.exception.UserDomainException;

public record Email(String value) {
    public Email {
        if (value == null || value.isBlank())
            throw new UserDomainException("Email cannot be blank");
        if (!value.matches("^[^@]+@[^@]+\\.[^@]+$"))
            throw new UserDomainException("Invalid email format: " + value);
    }
}
