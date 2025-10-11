package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.validation.UserValidator;


@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@Valid @RequestBody User newUser) {

        if (!UserValidator.isUserNameValid(newUser.getName())) {
            newUser.setName(newUser.getLogin());
        }

        log.debug("Создание пользователя: {}", newUser);

        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);

        return newUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) throws NotFoundException {
        log.debug("Обновление пользователя: {}", updatedUser);

        if (UserValidator.isUserNotFound(users, updatedUser)) {
            throw new NotFoundException("Такого пользователя нет");
        }

        if (!UserValidator.isUserNameValid(updatedUser.getName())) {
            updatedUser.setName(updatedUser.getLogin());
        }

        users.put(updatedUser.getId(), updatedUser);

        return updatedUser;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
