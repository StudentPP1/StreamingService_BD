package dev.studentpp1.streamingservice.users.domain.vo;

import dev.studentpp1.streamingservice.users.domain.exception.UserDomainException;
import dev.studentpp1.streamingservice.users.domain.model.vo.RawPassword;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RawPasswordTest {

    @Test
    void create_validPassword_success() {
        RawPassword password = new RawPassword("Test1234@");
        assertThat(password.value()).isEqualTo("Test1234@");
    }

    @Test
    void create_blankPassword_throwsDomainException() {
        assertThatThrownBy(() -> new RawPassword(""))
                .isInstanceOf(UserDomainException.class);
    }

    @Test
    void create_tooShortPassword_throwsDomainException() {
        assertThatThrownBy(() -> new RawPassword("Ab1@"))
                .isInstanceOf(UserDomainException.class)
                .hasMessageContaining("8 characters");
    }

    @Test
    void create_passwordWithoutUppercase_throwsDomainException() {
        assertThatThrownBy(() -> new RawPassword("test1234@"))
                .isInstanceOf(UserDomainException.class)
                .hasMessageContaining("Invalid password format");
    }

    @Test
    void create_passwordWithoutDigit_throwsDomainException() {
        assertThatThrownBy(() -> new RawPassword("TestTest@"))
                .isInstanceOf(UserDomainException.class)
                .hasMessageContaining("Invalid password format");
    }

    @Test
    void create_passwordWithoutSpecialChar_throwsDomainException() {
        assertThatThrownBy(() -> new RawPassword("TestTest1"))
                .isInstanceOf(UserDomainException.class)
                .hasMessageContaining("Invalid password format");
    }
}