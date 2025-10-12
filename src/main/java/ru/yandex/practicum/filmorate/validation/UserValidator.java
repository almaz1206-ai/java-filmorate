package ru.yandex.practicum.filmorate.validation;

import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class UserValidator {

    public static boolean isUserNameValid(String userName) {
        return Objects.nonNull(userName) && !userName.isBlank();
    }
}
