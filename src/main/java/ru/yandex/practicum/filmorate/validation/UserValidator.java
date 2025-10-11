package ru.yandex.practicum.filmorate.validation;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Objects;

@UtilityClass
public class UserValidator {
    public static boolean isUserNotFound(Map<Integer, User> users, User user) {
        return Objects.isNull(users.get(user.getId()));
    }

    public static boolean isUserNameValid(String userName) {
        return Objects.nonNull(userName) && !userName.isBlank();
    }
}
