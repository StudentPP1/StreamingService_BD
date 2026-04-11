package dev.studentpp1.streamingservice.users.domain;

import dev.studentpp1.streamingservice.users.domain.exception.UserAlreadyExistsException;
import dev.studentpp1.streamingservice.users.domain.factory.UserFactory;
import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.domain.model.vo.HashedPassword;
import dev.studentpp1.streamingservice.users.domain.model.vo.RawPassword;
import dev.studentpp1.streamingservice.users.domain.port.PasswordHasher;
import dev.studentpp1.streamingservice.users.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserFactoryTest {

    private UserRepository userRepository;
    private PasswordHasher passwordHasher;
    private UserFactory userFactory;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordHasher = mock(PasswordHasher.class);
        userFactory = new UserFactory(userRepository, passwordHasher);
    }

    @Test
    void create_validUser_success() {
        when(userRepository.existsByEmail("ivan@example.com")).thenReturn(false);
        when(passwordHasher.hash(any(RawPassword.class)))
                .thenReturn(new HashedPassword("hashed_pass"));

        User user = userFactory.create(
                "Ivan", "Petrenko",
                "ivan@example.com", "Test1234@",
                LocalDate.of(2000, 1, 1)
        );

        assertThat(user.getName()).isEqualTo("Ivan");
        assertThat(user.getEmail()).isEqualTo("ivan@example.com");
        assertThat(user.getId()).isNull();
        verify(userRepository).existsByEmail("ivan@example.com");
        verify(passwordHasher).hash(any(RawPassword.class));
    }

    @Test
    void create_duplicateEmail_throwsUserAlreadyExistsException() {
        when(userRepository.existsByEmail("ivan@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userFactory.create(
                "Ivan", "Petrenko",
                "ivan@example.com", "Test1234@",
                LocalDate.of(2000, 1, 1)
        )).isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("ivan@example.com");

        verify(passwordHasher, never()).hash(any());
    }
}