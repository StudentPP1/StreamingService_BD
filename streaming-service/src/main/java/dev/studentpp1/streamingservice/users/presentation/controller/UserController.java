package dev.studentpp1.streamingservice.users.presentation.controller;

import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.auth.service.AuthService;
import dev.studentpp1.streamingservice.users.application.command.DeleteUserCommand;
import dev.studentpp1.streamingservice.users.application.command.UpdateUserCommand;
import dev.studentpp1.streamingservice.users.application.command.UserCommandHandler;
import dev.studentpp1.streamingservice.users.application.query.GetCurrentUserInfoQuery;
import dev.studentpp1.streamingservice.users.application.query.UserQueryHandler;
import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.application.dto.UpdateUserRequest;
import dev.studentpp1.streamingservice.users.presentation.dto.UserDto;
import dev.studentpp1.streamingservice.users.presentation.mapper.UserPresentationMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCommandHandler userCommandHandler;
    private final UserQueryHandler userQueryHandler;
    private final AuthService authService;
    private final UserPresentationMapper userMapper;

    @PostMapping("/update")
    public ResponseEntity<UserDto> updateUser(
            @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        User user = userCommandHandler.handle(new UpdateUserCommand(request, currentUser.getUsername()));
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping("/info")
    public ResponseEntity<UserDto> getInfo(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        User user = userQueryHandler.handle(new GetCurrentUserInfoQuery(currentUser.getUsername()));
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(
            HttpServletRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        userCommandHandler.handle(new DeleteUserCommand(currentUser.getUsername()));
        authService.logout(request);
        return ResponseEntity.ok().build();
    }
}