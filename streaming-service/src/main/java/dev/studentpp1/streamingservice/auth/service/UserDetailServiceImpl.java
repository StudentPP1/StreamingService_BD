package dev.studentpp1.streamingservice.auth.service;

import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.auth.domain.model.AuthUserCredentials;
import dev.studentpp1.streamingservice.auth.domain.port.AuthUsersPort;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final AuthUsersPort authUsersPort;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        AuthUserCredentials user = authUsersPort.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new AuthenticatedUser(
                user.id(),
                user.email(),
                user.password(),
                List.of(new SimpleGrantedAuthority(user.role()))
        );
    }
}
