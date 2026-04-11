package dev.studentpp1.streamingservice.users.domain.factory;

import dev.studentpp1.streamingservice.users.domain.exception.UserAlreadyExistsException;
import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.domain.model.vo.Email;
import dev.studentpp1.streamingservice.users.domain.model.vo.HashedPassword;
import dev.studentpp1.streamingservice.users.domain.model.vo.RawPassword;
import dev.studentpp1.streamingservice.users.domain.port.PasswordHasher;
import dev.studentpp1.streamingservice.users.domain.repository.UserRepository;

import java.time.LocalDate;

public class UserFactory {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserFactory(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public User create(String name, String surname, String emailInput,
                       String passwordInput, LocalDate birthday) {
        Email email = new Email(emailInput);
        RawPassword rawPassword = new RawPassword(passwordInput);
        if (userRepository.existsByEmail(email.value())) {
            throw new UserAlreadyExistsException(email.value());
        }
        HashedPassword hashedPassword = passwordHasher.hash(rawPassword);
        return User.create(name, surname, email, hashedPassword, birthday);
    }
}