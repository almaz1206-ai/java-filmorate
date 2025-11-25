package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendShip.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        checkUserIsNotNull(user.getId());

        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Integer id) {
        checkUserIsNotNull(id);

        return userStorage.getUserById(id);
    }

    public Collection<User> getUserFriends(Integer id) {
        checkUserIsNotNull(id);
        return userStorage.getUserFriends(id);
    }

    public void addFriendToUser(Integer userId, Integer friendId) {
        checkUserIsNotNull(userId);
        checkUserIsNotNull(friendId);

        friendshipStorage.addFriend(userId, friendId);
    }

    public void deleteFriendFromUser(Integer userId, Integer friendId) {
        checkUserIsNotNull(userId);
        checkUserIsNotNull(friendId);

        friendshipStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> getCommonFriends(Integer userId, Integer otherUserId) {
        checkUserIsNotNull(userId);
        checkUserIsNotNull(otherUserId);

        return userStorage.getCommonFriends(userId, otherUserId);
    }

    private void checkUserIsNotNull(Integer id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователя с id %s нет", id));
        }
    }
}
