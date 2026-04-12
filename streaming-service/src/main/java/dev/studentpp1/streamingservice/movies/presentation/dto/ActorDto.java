package dev.studentpp1.streamingservice.movies.presentation.dto;

public record ActorDto(
        Long id,
        String name,
        String surname,
        String biography
) {}