package dev.studentpp1.streamingservice.auth.infrastructure.adapter;

import dev.studentpp1.streamingservice.auth.domain.model.AuthRegistrationData;
import dev.studentpp1.streamingservice.auth.domain.model.AuthUserCredentials;
import dev.studentpp1.streamingservice.auth.domain.port.AuthUsersPort;
import dev.studentpp1.streamingservice.auth.internal.acl.AuthUsersAclTranslator;
import dev.studentpp1.streamingservice.users.api.auth.UsersAuthApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsersAuthPortAdapter implements AuthUsersPort {

    private final UsersAuthApi usersAuthApi;
    private final AuthUsersAclTranslator aclTranslator;

    @Override
    public void create(AuthRegistrationData registrationData) {
        usersAuthApi.create(aclTranslator.toExternalCreateRequest(registrationData));
    }

    @Override
    public Optional<AuthUserCredentials> findByEmail(String email) {
        return usersAuthApi.findByEmail(email).map(aclTranslator::toInternalCredentials);
    }
}

