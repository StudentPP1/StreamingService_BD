package dev.studentpp1.streamingservice.users.infrastructure.adapter;

import dev.studentpp1.streamingservice.users.api.auth.UsersAuthApi;
import dev.studentpp1.streamingservice.users.api.auth.UsersAuthUserView;
import dev.studentpp1.streamingservice.users.api.auth.UsersCreateUserRequest;
import dev.studentpp1.streamingservice.users.application.command.CreateUserCommand;
import dev.studentpp1.streamingservice.users.application.command.UserCommandHandler;
import dev.studentpp1.streamingservice.users.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsersAuthApiAdapter implements UsersAuthApi {

    private final UserCommandHandler userCommandHandler;
    private final UserRepository userRepository;

    @Override
    public void create(UsersCreateUserRequest request) {
        userCommandHandler.handle(new CreateUserCommand(
                request.name(),
                request.surname(),
                request.email(),
                request.password(),
                request.birthday()
        ));
    }

    @Override
    public Optional<UsersAuthUserView> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new UsersAuthUserView(
                        user.getId(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getRole().name()
                ));
    }
}

