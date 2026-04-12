package dev.studentpp1.streamingservice.users.application.command;

import dev.studentpp1.streamingservice.users.domain.exception.UserNotFoundException;
import dev.studentpp1.streamingservice.users.domain.model.Role;
import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.domain.model.vo.Email;
import dev.studentpp1.streamingservice.users.domain.model.vo.HashedPassword;
import dev.studentpp1.streamingservice.users.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DeleteUserHandlerTest {

    private UserRepository userRepository;
    private DeleteUserHandler handler;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        handler = new DeleteUserHandler(userRepository);
    }

    @Test
    void handle_userNotFound_throwsUserNotFoundException() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new DeleteUserCommand("missing@example.com")))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void handle_validCommand_softDeletesAndSavesUser() {
        User user = User.restore(
                1L,
                "Ivan",
                "Petrenko",
                new Email("ivan@example.com"),
                new HashedPassword("hashed"),
                LocalDate.of(2000, 1, 1),
                Role.ROLE_USER,
                false
        );
        when(userRepository.findByEmail("ivan@example.com")).thenReturn(Optional.of(user));

        handler.handle(new DeleteUserCommand("ivan@example.com"));

        assertThat(user.isDeleted()).isTrue();
        verify(userRepository).save(user);
    }
}

