package dev.studentpp1.streamingservice.movies.application.command.actor;

public record UpdateActorCommand(Long id, ActorCreateRequest request) {
}
