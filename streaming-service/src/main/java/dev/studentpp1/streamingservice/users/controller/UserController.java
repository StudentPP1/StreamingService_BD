package dev.studentpp1.streamingservice.users.controller;

import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.auth.service.AuthService;
import dev.studentpp1.streamingservice.users.dto.UpdateUserRequest;
import dev.studentpp1.streamingservice.users.dto.UserDto;
import dev.studentpp1.streamingservice.users.mapper.UserDtoMapper;
import dev.studentpp1.streamingservice.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @PostMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody UpdateUserRequest request) {
        UserDto userDto = userDtoMapper.toUserDto(userService.updateUser(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @GetMapping("/info")
    public ResponseEntity<UserDto> getInfo(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        UserDto userDto = userDtoMapper.toUserDto(userService.getInfo(currentUser));
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(HttpServletRequest request,
                                           @AuthenticationPrincipal AuthenticatedUser currentUser) {
        userService.softDeleteUser(currentUser);
        authService.logout(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
