package dev.studentpp1.streamingservice.users.presentation.controller;

import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.auth.service.AuthService;
import dev.studentpp1.streamingservice.users.application.command.DeleteUserCommand;
import dev.studentpp1.streamingservice.users.application.command.UpdateUserCommand;
import dev.studentpp1.streamingservice.users.application.command.UserCommandHandler;
import dev.studentpp1.streamingservice.users.application.query.GetCurrentUserInfoQuery;
import dev.studentpp1.streamingservice.users.application.query.UserQueryHandler;
import dev.studentpp1.streamingservice.users.application.query.readmodel.UserReadModel;
import dev.studentpp1.streamingservice.users.presentation.dto.UpdateUserRequest;
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
    public ResponseEntity<Void> updateUser(
            @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        userCommandHandler.handle(new UpdateUserCommand(
                request.name(),
                request.surname(),
                currentUser.getUsername()
        ));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/info")
    public ResponseEntity<UserReadModel> getInfo(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        UserReadModel user = userQueryHandler.handle(new GetCurrentUserInfoQuery(currentUser.getUsername()));
        return ResponseEntity.ok(user);
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