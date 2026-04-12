package dev.studentpp1.streamingservice.users.integration;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.users.application.query.GetCurrentUserInfoQuery;
import dev.studentpp1.streamingservice.users.application.query.UserQueryHandler;
import dev.studentpp1.streamingservice.users.application.query.readmodel.UserReadModel;
import dev.studentpp1.streamingservice.users.domain.exception.UserNotFoundException;
import dev.studentpp1.streamingservice.users.domain.model.Role;
import dev.studentpp1.streamingservice.users.infrastructure.entity.UserEntity;
import dev.studentpp1.streamingservice.users.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserQueryHandlerIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired
    private UserQueryHandler userQueryHandler;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE payment, user_subscription, users RESTART IDENTITY CASCADE");
    }

    @Test
    void handle_existingUser_returnsReadModel() {
        userJpaRepository.save(UserEntity.builder()
                .name("Ivan")
                .surname("Petrenko")
                .email("ivan@example.com")
                .password("hashed")
                .birthday(LocalDate.of(2000, 1, 1))
                .role(Role.ROLE_USER)
                .build());

        UserReadModel result = userQueryHandler.handle(new GetCurrentUserInfoQuery("ivan@example.com"));

        assertThat(result.email()).isEqualTo("ivan@example.com");
        assertThat(result.name()).isEqualTo("Ivan");
        assertThat(result.role()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    void handle_missingUser_throwsUserNotFoundException() {
        assertThatThrownBy(() -> userQueryHandler.handle(new GetCurrentUserInfoQuery("missing@example.com")))
                .isInstanceOf(UserNotFoundException.class);
    }
}

