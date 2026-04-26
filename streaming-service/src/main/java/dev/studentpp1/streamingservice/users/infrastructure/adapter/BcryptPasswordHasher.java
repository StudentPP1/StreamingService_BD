package dev.studentpp1.streamingservice.users.infrastructure.adapter;

import dev.studentpp1.streamingservice.users.domain.model.vo.HashedPassword;
import dev.studentpp1.streamingservice.users.domain.model.vo.RawPassword;
import dev.studentpp1.streamingservice.users.domain.port.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptPasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public BcryptPasswordHasher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public HashedPassword hash(RawPassword rawPassword) {
        return new HashedPassword(passwordEncoder.encode(rawPassword.value()));
    }
}
