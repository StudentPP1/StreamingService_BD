package dev.studentpp1.streamingservice.movies.presentation.dto;

public record DirectorDto(
        Long id,
        String name,
        String surname,
        String biography
) {}