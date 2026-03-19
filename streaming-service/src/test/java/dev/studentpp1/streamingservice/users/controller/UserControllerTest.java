package dev.studentpp1.streamingservice.users.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.auth.persistence.Role;
import dev.studentpp1.streamingservice.auth.service.AuthService;
import dev.studentpp1.streamingservice.common.config.GlobalExceptionHandler;
import dev.studentpp1.streamingservice.users.dto.UpdateUserRequest;
import dev.studentpp1.streamingservice.users.dto.UserDto;
import dev.studentpp1.streamingservice.users.entity.AppUser;
import dev.studentpp1.streamingservice.users.mapper.UserDtoMapper;
import dev.studentpp1.streamingservice.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private AppUser createTestUser(Long id, String email) {
        return AppUser.builder()
                .id(id)
                .email(email)
                .name("John")
                .surname("Doe")
                .birthday(LocalDate.of(1990, 5, 15))
                .role(Role.ROLE_USER)
                .build();
    }

    private AuthenticatedUser createAuthenticatedUser(Long id, String email) {
        AppUser appUser = createTestUser(id, email);
        return new AuthenticatedUser(appUser);
    }

    private UserDto createTestUserDto() {
        return new UserDto(
                "John",
                "Doe",
                "user@test.com",
                LocalDate.of(1990, 5, 15),
                Role.ROLE_USER
        );
    }

    @Test
    void updateUser_withValidRequest_returnsCreatedAndUserDto() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("John", "Doe", LocalDate.of(1990, 5, 15));
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");
        AppUser updatedAppUser = createTestUser(42L, "user@test.com");
        UserDto expectedDto = createTestUserDto();

        when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn(updatedAppUser);
        when(userDtoMapper.toUserDto(updatedAppUser)).thenReturn(expectedDto);

        mockMvc.perform(post("/api/users/update")
                        .with(user(authenticatedUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.surname", is("Doe")))
                .andExpect(jsonPath("$.email", is("user@test.com")))
                .andExpect(jsonPath("$.birthday", is("1990-05-15")));

        verify(userService).updateUser(any(UpdateUserRequest.class));
        verify(userDtoMapper).toUserDto(updatedAppUser);
    }

    @Test
    void getInfo_withValidAuth_returnsUserDto() throws Exception {
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");
        AppUser appUser = authenticatedUser.getAppUser();
        UserDto expectedDto = createTestUserDto();

        when(userService.getInfo(any(AuthenticatedUser.class))).thenReturn(appUser);
        when(userDtoMapper.toUserDto(appUser)).thenReturn(expectedDto);

        mockMvc.perform(get("/api/users/info")
                        .with(user(authenticatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("user@test.com")));

        verify(userService).getInfo(any(AuthenticatedUser.class));
        verify(userDtoMapper).toUserDto(appUser);
    }

    @Test
    void deleteUser_withValidAuth_returnsOk() throws Exception {
        AuthenticatedUser authenticatedUser = createAuthenticatedUser(42L, "user@test.com");

        doNothing().when(userService).softDeleteUser(any(AuthenticatedUser.class));
        doNothing().when(authService).logout(any(HttpServletRequest.class));

        mockMvc.perform(delete("/api/users")
                        .with(user(authenticatedUser)))
                .andExpect(status().isOk());

        verify(userService).softDeleteUser(any(AuthenticatedUser.class));
        verify(authService).logout(any(HttpServletRequest.class));
    }
}