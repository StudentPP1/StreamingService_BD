package dev.studentpp1.streamingservice.users.application.usecase;

import dev.studentpp1.streamingservice.auth.dto.RegisterUserRequest;
import dev.studentpp1.streamingservice.users.domain.exception.UserNotFoundException;
import dev.studentpp1.streamingservice.users.domain.factory.UserFactory;
import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.domain.model.vo.Email;
import dev.studentpp1.streamingservice.users.domain.repository.UserRepository;
import dev.studentpp1.streamingservice.users.application.dto.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserFactory userFactory;

    @Transactional
    public User createUser(RegisterUserRequest request) {
        User user = userFactory.create(
                request.name(),
                request.surname(),
                request.email(),
                request.password(),
                request.birthday()
        );
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(UpdateUserRequest request, String currentUserEmail) {
        Email emailVo = new Email(currentUserEmail);
        User user = userRepository.findByEmail(emailVo.value())
                .orElseThrow(() -> new UserNotFoundException(currentUserEmail));
        user.update(request.name(), request.surname());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getInfo(String currentUserEmail) {
        Email emailVo = new Email(currentUserEmail);
        return userRepository.findByEmail(emailVo.value())
                .orElseThrow(() -> new UserNotFoundException(currentUserEmail));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        Email emailVo = new Email(email);
        return userRepository.findByEmail(emailVo.value())
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public void softDeleteUser(String currentUserEmail) {
        Email email = new Email(currentUserEmail);
        User user = userRepository.findByEmail(email.value())
                .orElseThrow(() -> new UserNotFoundException(currentUserEmail));
        user.softDelete();
        userRepository.save(user);
    }
}