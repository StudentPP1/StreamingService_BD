package dev.studentpp1.streamingservice.auth.controller;

import dev.studentpp1.streamingservice.auth.application.cqs.AuthCqs.LoginUserCommand;
import dev.studentpp1.streamingservice.auth.application.cqs.AuthCqs.LogoutCommand;
import dev.studentpp1.streamingservice.auth.application.cqs.AuthCqs.RegisterUserCommand;
import dev.studentpp1.streamingservice.auth.application.cqs.AuthCommandHandler;
import dev.studentpp1.streamingservice.auth.dto.LoginUserRequest;
import dev.studentpp1.streamingservice.auth.dto.RegisterUserRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthCommandHandler authCommandHandler;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterUserRequest request,
                                         HttpServletRequest httpServletRequest) throws Exception {
        authCommandHandler.handle(new RegisterUserCommand(request), httpServletRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginUserRequest request,
                                      HttpServletRequest httpServletRequest) {
        authCommandHandler.handle(new LoginUserCommand(request), httpServletRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authCommandHandler.handle(new LogoutCommand(), request);
        return ResponseEntity.noContent().build();
    }
}
