package dev.studentpp1.streamingservice.users.application;

import dev.studentpp1.streamingservice.auth.dto.RegisterUserRequest;
import dev.studentpp1.streamingservice.users.application.usecase.UserService;
import dev.studentpp1.streamingservice.users.domain.exception.UserNotFoundException;
import dev.studentpp1.streamingservice.users.domain.factory.UserFactory;
import dev.studentpp1.streamingservice.users.domain.model.Role;
import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.domain.model.vo.Email;
import dev.studentpp1.streamingservice.users.domain.model.vo.HashedPassword;
import dev.studentpp1.streamingservice.users.domain.repository.UserRepository;
import dev.studentpp1.streamingservice.users.application.dto.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserFactory userFactory;
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userFactory = mock(UserFactory.class);
        userService = new UserService(userRepository, userFactory);

        testUser = User.restore(
                1L, "Ivan", "Petrenko",
                new Email("ivan@example.com"),
                new HashedPassword("hashed_pass"),
                LocalDate.of(2000, 1, 1),
                Role.ROLE_USER, false
        );
    }

    @Test
    void createUser_delegatesToFactoryAndSaves() {
        RegisterUserRequest request = new RegisterUserRequest(
                "Ivan", "Petrenko", "ivan@example.com",
                "Test1234@", LocalDate.of(2000, 1, 1));

        when(userFactory.create("Ivan", "Petrenko", "ivan@example.com",
                "Test1234@", LocalDate.of(2000, 1, 1))).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.createUser(request);

        assertThat(result.getEmail()).isEqualTo("ivan@example.com");
        verify(userFactory).create("Ivan", "Petrenko", "ivan@example.com",
                "Test1234@", LocalDate.of(2000, 1, 1));
        verify(userRepository).save(testUser);
    }

    @Test
    void getInfo_userExists_returnsUser() {
        when(userRepository.findByEmail("ivan@example.com")).thenReturn(Optional.of(testUser));

        User result = userService.getInfo("ivan@example.com");

        assertThat(result.getName()).isEqualTo("Ivan");
        verify(userRepository).findByEmail("ivan@example.com");
    }

    @Test
    void getInfo_userNotFound_throwsUserNotFoundException() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getInfo("missing@example.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateUser_validData_updatesAndSaves() {
        when(userRepository.findByEmail("ivan@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        UpdateUserRequest request = new UpdateUserRequest("Mykola", "Kovalenko", LocalDate.of(2000, 1, 1));
        User result = userService.updateUser(request, "ivan@example.com");

        assertThat(result.getName()).isEqualTo("Mykola");
        assertThat(result.getSurname()).isEqualTo("Kovalenko");
        verify(userRepository).save(testUser);
    }

    @Test
    void findById_exists_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_notFound_throwsUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void softDeleteUser_marksUserDeletedAndSaves() {
        when(userRepository.findByEmail("ivan@example.com")).thenReturn(Optional.of(testUser));

        userService.softDeleteUser("ivan@example.com");

        assertThat(testUser.isDeleted()).isTrue();
        verify(userRepository).save(testUser);
    }
}