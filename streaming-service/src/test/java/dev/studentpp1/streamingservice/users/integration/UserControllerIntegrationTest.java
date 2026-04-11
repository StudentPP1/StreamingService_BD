package dev.studentpp1.streamingservice.users.integration;

import dev.studentpp1.streamingservice.AbstractPostgresContainerTest;
import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.users.domain.model.Role;
import dev.studentpp1.streamingservice.users.infrastructure.entity.UserEntity;
import dev.studentpp1.streamingservice.users.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserControllerIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE payment, user_subscription, users RESTART IDENTITY CASCADE");
    }

    private static final String TEST_EMAIL = "ivan@example.com";

    private AuthenticatedUser principal() {
        return new AuthenticatedUser(1L, TEST_EMAIL, "pass",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private void saveTestUser() {
        userJpaRepository.save(UserEntity.builder()
                .name("Ivan")
                .surname("Petrenko")
                .email(TEST_EMAIL)
                .password("hashed")
                .birthday(LocalDate.of(2000, 1, 1))
                .role(Role.ROLE_USER)
                .build());
    }

    @Test
    void getInfo_unauthenticated_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/users/info"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getInfo_authenticated_returnsOk() throws Exception {
        saveTestUser();

        mockMvc.perform(get("/api/users/info")
                        .with(user(principal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    void updateUser_authenticated_returnsOk() throws Exception {
        saveTestUser();

        String json = """
                {"name":"Updated","surname":"Name"}
                """;

        mockMvc.perform(post("/api/users/update")
                        .with(user(principal()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deleteUser_authenticated_returnsOk() throws Exception {
        saveTestUser();

        mockMvc.perform(delete("/api/users")
                        .with(user(principal())))
                .andExpect(status().isOk());
    }
}