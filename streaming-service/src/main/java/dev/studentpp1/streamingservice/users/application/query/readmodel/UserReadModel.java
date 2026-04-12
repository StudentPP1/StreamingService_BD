package dev.studentpp1.streamingservice.users.application.query.readmodel;

import dev.studentpp1.streamingservice.users.domain.model.Role;

import java.time.LocalDate;

public record UserReadModel(
        Long id,
        String name,
        String surname,
        String email,
        String password,
        LocalDate birthday,
        Role role,
        boolean deleted
) {
}

