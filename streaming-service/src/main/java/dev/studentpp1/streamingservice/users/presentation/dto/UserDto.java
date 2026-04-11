package dev.studentpp1.streamingservice.users.presentation.dto;

import dev.studentpp1.streamingservice.users.domain.model.Role;

import java.time.LocalDate;

public record UserDto(
        String name,
        String surname,
        String email,
        LocalDate birthday,
        Role role
) {
}
