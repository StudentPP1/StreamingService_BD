package dev.studentpp1.streamingservice.users.application.cqs;

import dev.studentpp1.streamingservice.users.application.cqs.UserCqs.*;
import dev.studentpp1.streamingservice.users.domain.exception.UserNotFoundException;
import dev.studentpp1.streamingservice.users.domain.repository.UserRepository;
import dev.studentpp1.streamingservice.users.presentation.dto.UserDto;
import dev.studentpp1.streamingservice.users.presentation.mapper.UserPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryHandler {
    private final UserRepository userRepository;
    private final UserPresentationMapper mapper;

    public UserDto handle(GetCurrentUserQuery query) {
        return mapper.toDto(userRepository.findByEmail(query.currentUserEmail())
                .orElseThrow(() -> new UserNotFoundException(query.currentUserEmail())));
    }
}
