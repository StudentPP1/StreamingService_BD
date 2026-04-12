package dev.studentpp1.streamingservice.users.application.query;

import dev.studentpp1.streamingservice.users.application.query.readmodel.UserReadModel;
import dev.studentpp1.streamingservice.users.application.query.repo.UserReadRepository;
import dev.studentpp1.streamingservice.users.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserQueryHandler {

    private final UserReadRepository userReadRepository;

    public UserReadModel handle(GetCurrentUserInfoQuery query) {
        return userReadRepository.findByEmail(query.currentUserEmail())
                .orElseThrow(() -> new UserNotFoundException(query.currentUserEmail()));
    }
}

