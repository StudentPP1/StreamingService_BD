package dev.studentpp1.streamingservice.auth.service;

import dev.studentpp1.streamingservice.auth.presentation.dto.LoginUserRequest;
import dev.studentpp1.streamingservice.auth.presentation.dto.RegisterUserRequest;
import dev.studentpp1.streamingservice.auth.domain.model.AuthRegistrationData;
import dev.studentpp1.streamingservice.auth.domain.port.AuthUsersPort;
import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthUsersPort authUsersPort;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpSession httpSession;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_createsUserAndSession() {
        RegisterUserRequest request = new RegisterUserRequest(
                "Test",
                "User",
                "test@example.com",
                "password123",
                java.time.LocalDate.of(2000, 1, 1)
        );

        Authentication authentication = mock(Authentication.class);
        AuthenticatedUser registeredPrincipal = new AuthenticatedUser(
                1L,
                "test@example.com",
                "encoded_pass",
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
        );
        when(authentication.getPrincipal()).thenReturn(registeredPrincipal);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(httpServletRequest.getSession(true)).thenReturn(httpSession);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        authService.register(request, httpServletRequest);

        verify(authUsersPort).create(any(AuthRegistrationData.class));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

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