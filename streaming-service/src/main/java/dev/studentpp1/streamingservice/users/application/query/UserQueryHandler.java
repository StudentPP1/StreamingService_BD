package dev.studentpp1.streamingservice.users.application.query;

import dev.studentpp1.streamingservice.users.application.usecase.UserService;
import dev.studentpp1.streamingservice.users.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserQueryHandler {

    private final UserService userService;

    public User handle(GetCurrentUserInfoQuery query) {
        return userService.getInfo(query.currentUserEmail());
    }
}

