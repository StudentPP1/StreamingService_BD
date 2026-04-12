package dev.studentpp1.streamingservice.movies.presentation.dto;

public record ActorFilmographyDto(
        Long movieId,
        String movieTitle,
        Integer movieYear,
        String characterName
) {}