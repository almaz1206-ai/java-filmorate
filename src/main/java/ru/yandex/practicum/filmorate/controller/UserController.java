package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import ru.yandex.practicum.filmorate.validation.UserValidator;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
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

        if (!users.containsKey(updatedUser.getId())) {
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

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
