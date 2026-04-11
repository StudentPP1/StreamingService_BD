package dev.studentpp1.streamingservice.auth.service;

import dev.studentpp1.streamingservice.auth.dto.LoginUserRequest;
import dev.studentpp1.streamingservice.auth.dto.RegisterUserRequest;
import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.users.application.usecase.UserService;
import dev.studentpp1.streamingservice.users.domain.model.Role;
import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.domain.model.vo.Email;
import dev.studentpp1.streamingservice.users.domain.model.vo.HashedPassword;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpSession httpSession;

    private User testUser;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        testUser = User.restore(
                1L,
                "Ivan",
                "Ivanov",
                new Email("test@example.com"),
                new HashedPassword("encoded_pass"),
                LocalDate.of(2000, 1, 1),
                Role.ROLE_USER,
                false
        );
    }

    @Test
    void register_createsUserAndSession() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest(
                "test@example.com",
                "password123",
                "Test",
                "User",
                LocalDate.of(2000, 1, 1)
        );

        when(userService.createUser(request)).thenReturn(testUser);
        when(httpServletRequest.getSession(true)).thenReturn(httpSession);

        authService.register(request, httpServletRequest);

        verify(userService).createUser(request);

        ArgumentCaptor<SecurityContext> contextCaptor = ArgumentCaptor.forClass(SecurityContext.class);
        verify(httpSession).setAttribute(
                eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY),
                contextCaptor.capture()
        );

        SecurityContext savedContext = contextCaptor.getValue();
        assertThat(savedContext.getAuthentication().isAuthenticated()).isTrue();

        AuthenticatedUser principal = (AuthenticatedUser) savedContext.getAuthentication().getPrincipal();
        assertThat(principal.getUsername()).isEqualTo("test@example.com");
    }

    @Test
    void login_authenticatesUserAndCreatesSession() {
        LoginUserRequest request = new LoginUserRequest("test@example.com", "password123");
        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(httpServletRequest.getSession(true)).thenReturn(httpSession);
        authService.login(request, httpServletRequest);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(httpSession).setAttribute(eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY), any());
    }

    @Test
    void logout_invalidatesSessionAndClearsContext() {
        when(httpServletRequest.getSession(false)).thenReturn(httpSession);
        authService.logout(httpServletRequest);
        verify(httpSession).invalidate();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}