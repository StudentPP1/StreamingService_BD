package dev.studentpp1.streamingservice.users.application.command;

import dev.studentpp1.streamingservice.users.domain.factory.UserFactory;
import dev.studentpp1.streamingservice.users.domain.model.Role;
import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.domain.model.vo.Email;
import dev.studentpp1.streamingservice.users.domain.model.vo.HashedPassword;
import dev.studentpp1.streamingservice.users.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

class CreateUserHandlerTest {

    private UserRepository userRepository;
    private UserFactory userFactory;
    private CreateUserHandler handler;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userFactory = mock(UserFactory.class);
        handler = new CreateUserHandler(userRepository, userFactory);
    }

    @Test
    void handle_validCommand_createsAndSavesUser() {
        CreateUserCommand command = new CreateUserCommand(
                "Ivan", "Petrenko", "ivan@example.com", "Test1234@", LocalDate.of(2000, 1, 1)
        );
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

        when(userFactory.create("Ivan", "Petrenko", "ivan@example.com", "Test1234@", LocalDate.of(2000, 1, 1)))
                .thenReturn(user);

        handler.handle(command);

        verify(userFactory).create("Ivan", "Petrenko", "ivan@example.com", "Test1234@", LocalDate.of(2000, 1, 1));
        verify(userRepository).save(user);
    }
}

