package dev.studentpp1.streamingservice.auth.service;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.users.dto.LoginUserRequest;
import dev.studentpp1.streamingservice.users.dto.RegisterUserRequest;
import dev.studentpp1.streamingservice.users.entity.AppUser;
import dev.studentpp1.streamingservice.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        
        testUser = AppUser.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .name("Test")
                .surname("User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
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
        verify(httpServletRequest).getSession(true);
        
        ArgumentCaptor<SecurityContext> contextCaptor = ArgumentCaptor.forClass(SecurityContext.class);
        verify(httpSession).setAttribute(
                eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY),
                contextCaptor.capture()
        );

        SecurityContext savedContext = contextCaptor.getValue();
        assertThat(savedContext).isNotNull();
        assertThat(savedContext.getAuthentication()).isNotNull();
        assertThat(savedContext.getAuthentication().isAuthenticated()).isTrue();

        Object principal = savedContext.getAuthentication().getPrincipal();
        assertThat(principal).isInstanceOf(AuthenticatedUser.class);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) principal;
        assertThat(authenticatedUser.getUsername()).isEqualTo("test@example.com");
    }

    @Test
    void login_authenticatesUserAndCreatesSession() {
        LoginUserRequest request = new LoginUserRequest("test@example.com", "password123");

        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(httpServletRequest.getSession(true)).thenReturn(httpSession);

        authService.login(request, httpServletRequest);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor = 
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(tokenCaptor.capture());

        UsernamePasswordAuthenticationToken capturedToken = tokenCaptor.getValue();
        assertThat(capturedToken.getPrincipal()).isEqualTo("test@example.com");
        assertThat(capturedToken.getCredentials()).isEqualTo("password123");

        verify(httpServletRequest).getSession(true);
        verify(httpSession).setAttribute(
                eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY),
                any(SecurityContext.class)
        );
    }

    @Test
    void logout_invalidatesSessionAndClearsContext() {
        when(httpServletRequest.getSession(false)).thenReturn(httpSession);

        authService.logout(httpServletRequest);

        verify(httpServletRequest).getSession(false);
        verify(httpSession).invalidate();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void logout_whenNoSession_clearsContextOnly() {
        when(httpServletRequest.getSession(false)).thenReturn(null);

        authService.logout(httpServletRequest);

        verify(httpServletRequest).getSession(false);
        verify(httpSession, never()).invalidate();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
