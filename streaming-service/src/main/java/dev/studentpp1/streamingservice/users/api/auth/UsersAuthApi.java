package dev.studentpp1.streamingservice.users.api.auth;

import java.util.Optional;

public interface UsersAuthApi {
    void create(UsersCreateUserRequest request);

    Optional<UsersAuthUserView> findByEmail(String email);
}

