package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public User addUser(@Valid @RequestBody User newUser) {
        log.debug("Создание пользователя: {}", newUser);

        return userService.addUser(newUser);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.debug("Обновление пользователя: {}", updatedUser);

        return userService.updateUser(updatedUser);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return new ArrayList<>(userService.getAllUsers().values());
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") Integer id) {
        return userService.getUserFriends(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriendToUser(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        userService.addFriendToUser(id, friendId);
        userService.addFriendToUser(friendId, id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendFromUser(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        userService.deleteFriendFromUser(id, friendId);
        userService.deleteFriendFromUser(friendId, id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonUser(@PathVariable("id") Integer id, @PathVariable("otherId") Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
