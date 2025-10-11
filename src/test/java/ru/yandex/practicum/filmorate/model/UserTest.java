package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private User createValidUser() {
        return User.builder()
                .email("Bobby31@yahoo.com")
                .login("1sY7wWiBG5")
                .name("Joann Pollich")
                .birthday(LocalDate.of(1980, 5, 30))
                .build();
    }

    @Test
    void shouldCreateValidUser() {
        User user = createValidUser();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(),
                "Валидный пользователь не должен иметь нарушений. Нарушения: " + violations);
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        User user = createValidUser();
        user.setEmail("   ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(2, violations.size());
        Set<String> messages = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        assertTrue(messages.contains("email не может быть пустым"));
        assertTrue(messages.contains("Некорректный формат email"));
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        User user = createValidUser();
        user.setEmail(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("email не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenLoginIsBlank() {
        User user = createValidUser();
        user.setLogin("   ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(2, violations.size());

        Set<String> messages = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        assertTrue(messages.contains("Логин не может быть пустым"));
        assertTrue(messages.contains("Логин не может содержать пробелы"));
    }

    @Test
    void shouldFailWhenLoginContainsSpaces() {
        User user = createValidUser();
        user.setLogin("login with spaces");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        boolean hasSpaceViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("пробел"));
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenLoginHasMultipleSpaces() {
        User user = createValidUser();
        user.setLogin("login with spaces");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Логин не может содержать пробелы", violations.iterator().next().getMessage());
    }
}
