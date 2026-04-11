package dev.studentpp1.streamingservice.users.domain.model.vo;

import dev.studentpp1.streamingservice.users.domain.exception.UserDomainException;

public record HashedPassword(String value) {
    public HashedPassword {
        if (value == null || value.isBlank()) {
            throw new UserDomainException("Hashed password cannot be blank");
        }
    }
}
