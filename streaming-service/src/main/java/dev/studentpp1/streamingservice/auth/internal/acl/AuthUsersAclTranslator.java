package dev.studentpp1.streamingservice.auth.internal.acl;

import dev.studentpp1.streamingservice.auth.domain.model.AuthRegistrationData;
import dev.studentpp1.streamingservice.auth.domain.model.AuthUserCredentials;
import dev.studentpp1.streamingservice.users.api.auth.UsersAuthUserView;
import dev.studentpp1.streamingservice.users.api.auth.UsersCreateUserRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthUsersAclTranslator {

    public UsersCreateUserRequest toExternalCreateRequest(AuthRegistrationData data) {
        return new UsersCreateUserRequest(
                data.name(),
                data.surname(),
                data.email(),
                data.password(),
                data.birthday()
        );
    }

    public AuthUserCredentials toInternalCredentials(UsersAuthUserView userView) {
        return new AuthUserCredentials(
                userView.id(),
                userView.email(),
                userView.password(),
                userView.role()
        );
    }
}

