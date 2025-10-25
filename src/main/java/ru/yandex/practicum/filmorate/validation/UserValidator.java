package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Objects;

public class UserValidator {
    public static boolean isUserNotFound(Map<Integer, User> users, User user) {
        return Objects.isNull(user) || Objects.isNull(users.get(user.getId()));
    }
}
