package dev.studentpp1.streamingservice.auth.application.cqs;
import dev.studentpp1.streamingservice.auth.dto.LoginUserRequest;
import dev.studentpp1.streamingservice.auth.dto.RegisterUserRequest;
public final class AuthCqs {
    private AuthCqs() {}
    public record RegisterUserCommand(RegisterUserRequest request) {}
    public record LoginUserCommand(LoginUserRequest request) {}
    public record LogoutCommand() {}
}
