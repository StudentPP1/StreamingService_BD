package dev.studentpp1.streamingservice.users.application.cqs;

import dev.studentpp1.streamingservice.auth.dto.RegisterUserRequest;
import dev.studentpp1.streamingservice.users.application.usecase.UserService;
import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.presentation.dto.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCommandHandlerUnitTest {
    @Mock
    private UserService userService;
    @Mock
    private User user;
    private UserCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new UserCommandHandler(userService);
    }

    @Test
    void createUser_returnsCreatedId() {
        RegisterUserRequest request = new RegisterUserRequest(
                "Ivan", "Petrenko", "ivan@example.com", "Password123!", LocalDate.of(2000, 1, 1));
        when(userService.createUser(request)).thenReturn(user);
        when(user.getId()).thenReturn(11L);
        Long result = handler.handle(new UserCqs.CreateUserCommand(request));
        assertThat(result).isEqualTo(11L);
        verify(userService).createUser(request);
    }

    @Test
    void updateUser_returnsUpdatedId() {
        UpdateUserRequest request = new UpdateUserRequest("Updated", "User", LocalDate.of(2000, 1, 1));
        when(userService.updateUser(request, "ivan@example.com")).thenReturn(user);
        when(user.getId()).thenReturn(22L);
        Long result = handler.handle(new UserCqs.UpdateUserCommand(request, "ivan@example.com"));
        assertThat(result).isEqualTo(22L);
        verify(userService).updateUser(request, "ivan@example.com");
    }

    @Test
    void deleteUser_delegatesToService() {
        handler.handle(new UserCqs.DeleteUserCommand("ivan@example.com"));
        verify(userService).softDeleteUser("ivan@example.com");
    }
}

