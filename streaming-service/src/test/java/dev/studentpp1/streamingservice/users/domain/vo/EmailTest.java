package dev.studentpp1.streamingservice.users.domain.vo;

import dev.studentpp1.streamingservice.users.domain.exception.UserDomainException;
import dev.studentpp1.streamingservice.users.domain.model.vo.Email;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EmailTest {

    @Test
    void create_validEmail_success() {
        Email email = new Email("user@example.com");
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    void create_blankEmail_throwsDomainException() {
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(UserDomainException.class)
                .hasMessageContaining("blank");
    }

    @Test
    void create_nullEmail_throwsDomainException() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(UserDomainException.class);
    }

    @Test
    void create_emailWithoutAt_throwsDomainException() {
        assertThatThrownBy(() -> new Email("invalidemail.com"))
                .isInstanceOf(UserDomainException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    void create_emailWithoutDomain_throwsDomainException() {
        assertThatThrownBy(() -> new Email("user@"))
                .isInstanceOf(UserDomainException.class);
    }
}