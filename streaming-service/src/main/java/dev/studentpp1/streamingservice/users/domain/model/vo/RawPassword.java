package dev.studentpp1.streamingservice.users.domain.model.vo;

import dev.studentpp1.streamingservice.users.domain.exception.UserDomainException;

public record RawPassword(String value) {
    public RawPassword {
        if (value == null || value.isBlank())
            throw new UserDomainException("Password cannot be blank");
        if (value.length() < 8)
            throw new UserDomainException("Password must be at least 8 characters");
        if (!value.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"))
            throw new UserDomainException("Invalid password format: " + value);
    }
}
