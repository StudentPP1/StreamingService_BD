package dev.studentpp1.streamingservice.users.presentation.controller;

import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.auth.service.AuthService;
import dev.studentpp1.streamingservice.users.application.cqs.UserCqs.*;
import dev.studentpp1.streamingservice.users.application.cqs.UserCommandHandler;
import dev.studentpp1.streamingservice.users.application.cqs.UserQueryHandler;
import dev.studentpp1.streamingservice.users.presentation.dto.UpdateUserRequest;
import dev.studentpp1.streamingservice.users.presentation.dto.UserDto;
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

    @PostMapping("/update")
    public ResponseEntity<UserDto> updateUser(
            @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        userCommandHandler.handle(new UpdateUserCommand(request, currentUser.getUsername()));
        return ResponseEntity.ok(userQueryHandler.handle(new GetCurrentUserQuery(currentUser.getUsername())));
    }

    @GetMapping("/info")
    public ResponseEntity<UserDto> getInfo(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(userQueryHandler.handle(new GetCurrentUserQuery(currentUser.getUsername())));
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