package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User newUser) {

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        log.debug("Создание пользователя: {}", newUser);

        return userService.addUser(newUser);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.debug("Обновление пользователя: {}", updatedUser);

        if (updatedUser.getName() == null || updatedUser.getName().isBlank()) {
            updatedUser.setName(updatedUser.getLogin());
        }

        checkUserIsNotNull(updatedUser, updatedUser.getId());

        return userService.updateUser(updatedUser);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return new ArrayList<>(userService.getAllUsers().values());
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        User user = userService.getUserById(id);

        checkUserIsNotNull(user, id);

        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") Integer id) {
        User user = userService.getUserById(id);

        checkUserIsNotNull(user, id);

        return userService.getUserFriends(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriendToUser(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        User user = userService.getUserById(id);
        User friend = userService.getUserById(friendId);

        checkUserIsNotNull(user, id);
        checkUserIsNotNull(friend, friendId);

        userService.addFriendToUser(user, friend);
        userService.addFriendToUser(friend, user);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendFromUser(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        User user = userService.getUserById(id);
        User friend = userService.getUserById(friendId);

        checkUserIsNotNull(user, id);
        checkUserIsNotNull(friend, friendId);

        userService.deleteFriendFromUser(user, friend);
        userService.deleteFriendFromUser(friend, user);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonUser(@PathVariable("id") Integer id, @PathVariable("otherId") Integer otherId) {
        User user = userService.getUserById(id);
        User otherUser = userService.getUserById(otherId);

        checkUserIsNotNull(user, id);
        checkUserIsNotNull(otherUser, otherId);

        return userService.getCommonFriends(user, otherUser);
    }

    private void checkUserIsNotNull(User user, Integer id) {
        if (UserValidator.isUserNotFound(userService.getAllUsers(), user)) {
            throw new NotFoundException(String.format("Пользователя с id %s нет", id));
        }
    }

}
