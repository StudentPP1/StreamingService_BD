package dev.studentpp1.streamingservice.users.domain.port;

import dev.studentpp1.streamingservice.users.domain.model.vo.HashedPassword;
import dev.studentpp1.streamingservice.users.domain.model.vo.RawPassword;

public interface PasswordHasher {
    HashedPassword hash(RawPassword rawPassword);
}