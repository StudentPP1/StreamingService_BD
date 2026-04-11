package dev.studentpp1.streamingservice.users.domain.model;

import dev.studentpp1.streamingservice.users.domain.exception.UserDomainException;
import dev.studentpp1.streamingservice.users.domain.model.vo.Email;
import dev.studentpp1.streamingservice.users.domain.model.vo.HashedPassword;

import java.time.LocalDate;
import java.time.Period;

public class User {
    private final Long id;
    private String name;
    private String surname;
    private final Email email;
    private HashedPassword password;
    private LocalDate birthday;
    private Role role;
    private boolean deleted;

    private User(Long id, String name, String surname, Email email,
                 HashedPassword password, LocalDate birthday, Role role, boolean deleted) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.role = role;
        this.deleted = deleted;
    }

    public static User restore(Long id, String name, String surname, Email email,
                               HashedPassword password, LocalDate birthday,
                               Role role, boolean deleted) {
        return new User(id, name, surname, email, password, birthday, role, deleted);
    }

    public static User create(String name, String surname, Email email,
                              HashedPassword hashedPassword, LocalDate birthday) {
        if (name == null || name.isBlank())
            throw new UserDomainException("Name cannot be blank");
        int age = Period.between(birthday, LocalDate.now()).getYears();
        if (age < 18) {
            throw new UserDomainException("User must be older than 18 years");
        }
        return new User(null, name, surname, email,
                hashedPassword, birthday, Role.ROLE_USER, false);
    }

    public void update(String name, String surname) {
        if (name == null || name.isBlank())
            throw new UserDomainException("Name cannot be blank");
        if (surname == null || surname.isBlank())
            throw new UserDomainException("Surname cannot be blank");
        this.name = name;
        this.surname = surname;
    }

    public void softDelete() {
        this.deleted = true;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email.value();
    }

    public String getPassword() {
        return password.value();
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public Role getRole() {
        return role;
    }

    public boolean isDeleted() {
        return deleted;
    }
}