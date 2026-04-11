package dev.studentpp1.streamingservice.movies.presentation.dto.response;

public record PerformanceDto (
    Long id,
    String characterName,
    String description,

    Long actorId,
    Long movieId
){
}