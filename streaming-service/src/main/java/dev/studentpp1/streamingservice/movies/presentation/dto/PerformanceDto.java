package dev.studentpp1.streamingservice.movies.presentation.dto;

public record PerformanceDto (
    Long id,
    String characterName,
    String description,

    Long actorId,
    Long movieId
){
}