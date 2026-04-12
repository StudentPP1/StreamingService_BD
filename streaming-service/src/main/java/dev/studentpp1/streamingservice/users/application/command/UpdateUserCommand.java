package dev.studentpp1.streamingservice.users.application.command;

public record UpdateUserCommand(
		String name,
		String surname,
		String currentUserEmail
) {
}

