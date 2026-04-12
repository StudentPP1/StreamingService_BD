package dev.studentpp1.streamingservice.movies.application.command;

import dev.studentpp1.streamingservice.movies.application.dto.DirectorCreateRequest;

public record UpdateDirectorCommand(Long id, DirectorCreateRequest request) {
}

