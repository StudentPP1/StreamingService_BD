package dev.studentpp1.streamingservice.auth.application.cqs;

import dev.studentpp1.streamingservice.auth.dto.LoginUserRequest;
import dev.studentpp1.streamingservice.auth.dto.RegisterUserRequest;
import dev.studentpp1.streamingservice.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthCommandHandlerUnitTest {
    @Mock
    private AuthService authService;
    @Mock
    private HttpServletRequest httpServletRequest;
    private AuthCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new AuthCommandHandler(authService);
    }

    @Test
    void register_delegatesToAuthService() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest(
                "Ivan", "Petrenko", "ivan@example.com", "Password123!", LocalDate.of(2000, 1, 1));
        handler.handle(new AuthCqs.RegisterUserCommand(request), httpServletRequest);
        verify(authService).register(request, httpServletRequest);
    }

    @Test
    void login_delegatesToAuthService() {
        LoginUserRequest request = new LoginUserRequest("ivan@example.com", "Password123!");
        handler.handle(new AuthCqs.LoginUserCommand(request), httpServletRequest);
        verify(authService).login(request, httpServletRequest);
    }

    @Test
    void logout_delegatesToAuthService() {
        handler.handle(new AuthCqs.LogoutCommand(), httpServletRequest);
        verify(authService).logout(httpServletRequest);
    }
}

