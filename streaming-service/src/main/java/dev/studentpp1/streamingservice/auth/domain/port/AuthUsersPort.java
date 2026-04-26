package dev.studentpp1.streamingservice.auth.domain.port;

import dev.studentpp1.streamingservice.auth.domain.model.AuthRegistrationData;
import dev.studentpp1.streamingservice.auth.domain.model.AuthUserCredentials;

import java.util.Optional;

public interface AuthUsersPort {
    void create(AuthRegistrationData registrationData);

    Optional<AuthUserCredentials> findByEmail(String email);
}

