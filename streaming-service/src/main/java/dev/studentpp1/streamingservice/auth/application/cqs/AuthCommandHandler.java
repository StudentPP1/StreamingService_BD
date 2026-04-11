package dev.studentpp1.streamingservice.auth.application.cqs;
import dev.studentpp1.streamingservice.auth.application.cqs.AuthCqs.*;
import dev.studentpp1.streamingservice.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class AuthCommandHandler {
    private final AuthService authService;
    public void handle(RegisterUserCommand command, HttpServletRequest request) throws Exception {
        authService.register(command.request(), request);
    }
    public void handle(LoginUserCommand command, HttpServletRequest request) {
        authService.login(command.request(), request);
    }
    public void handle(LogoutCommand command, HttpServletRequest request) {
        authService.logout(request);
    }
}
