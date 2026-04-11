package dev.studentpp1.streamingservice.users.domain;

import dev.studentpp1.streamingservice.users.domain.exception.UserDomainException;
import dev.studentpp1.streamingservice.users.domain.model.Role;
import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.domain.model.vo.Email;
import dev.studentpp1.streamingservice.users.domain.model.vo.HashedPassword;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void create_validUser_success() {
        User user = User.create(
                "Ivan", "Petrenko",
                new Email("ivan@example.com"),
                new HashedPassword("hashed_pass"),
                LocalDate.of(2000, 1, 1)
        );

        assertThat(user.getName()).isEqualTo("Ivan");
        assertThat(user.getSurname()).isEqualTo("Petrenko");
        assertThat(user.getEmail()).isEqualTo("ivan@example.com");
        assertThat(user.getId()).isNull();
        assertThat(user.isDeleted()).isFalse();
        assertThat(user.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    void create_blankName_throwsDomainException() {
        assertThatThrownBy(() -> User.create(
                "", "Petrenko",
                new Email("ivan@example.com"),
                new HashedPassword("hashed_pass"),
                LocalDate.of(2000, 1, 1)
        )).isInstanceOf(UserDomainException.class)
                .hasMessageContaining("Name cannot be blank");
    }

    @Test
    void create_underageUser_throwsDomainException() {
        LocalDate birthday = LocalDate.now().minusYears(17);
        assertThatThrownBy(() -> User.create(
                "Ivan", "Petrenko",
                new Email("ivan@example.com"),
                new HashedPassword("hashed_pass"),
                birthday
        )).isInstanceOf(UserDomainException.class)
                .hasMessageContaining("18 years");
    }

    @Test
    void update_validData_success() {
        User user = User.restore(
                1L, "Ivan", "Petrenko",
                new Email("ivan@example.com"),
                new HashedPassword("hashed_pass"),
                LocalDate.of(2000, 1, 1),
                Role.ROLE_USER, false
        );

        user.update("Mykola", "Kovalenko");

        assertThat(user.getName()).isEqualTo("Mykola");
        assertThat(user.getSurname()).isEqualTo("Kovalenko");
    }

    @Test
    void update_blankName_throwsDomainException() {
        User user = User.restore(
                1L, "Ivan", "Petrenko",
                new Email("ivan@example.com"),
                new HashedPassword("hashed_pass"),
                LocalDate.of(2000, 1, 1),
                Role.ROLE_USER, false
        );

        assertThatThrownBy(() -> user.update("", "Kovalenko"))
                .isInstanceOf(UserDomainException.class);
    }

    @Test
    void update_blankSurname_throwsDomainException() {
        User user = User.restore(
                1L, "Ivan", "Petrenko",
                new Email("ivan@example.com"),
                new HashedPassword("hashed_pass"),
                LocalDate.of(2000, 1, 1),
                Role.ROLE_USER, false
        );

        assertThatThrownBy(() -> user.update("Ivan", ""))
                .isInstanceOf(UserDomainException.class);
    }

    @Test
    void softDelete_marksUserAsDeleted() {
        User user = User.restore(
                1L, "Ivan", "Petrenko",
                new Email("ivan@example.com"),
                new HashedPassword("hashed_pass"),
                LocalDate.of(2000, 1, 1),
                Role.ROLE_USER, false
        );

        user.softDelete();

        assertThat(user.isDeleted()).isTrue();
    }
}