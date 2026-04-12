package dev.studentpp1.streamingservice.movies.presentation.dto;

public record MovieCastDto(
        Long actorId,
        String actorName,
        String actorSurname,
        String characterName
) {}