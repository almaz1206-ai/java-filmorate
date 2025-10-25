package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public Map<Integer, User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public List<User> getUserFriends(Integer id) {
        User user = getUserById(id);
        Map<Integer, User> users = getAllUsers();

        return user
                .getFriends()
                .stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    public void addFriendToUser(User user, User friend) {
        user.getFriends().add(friend.getId());
        userStorage.updateUser(user);
    }

    public void deleteFriendFromUser(User user, User friend) {
        user.getFriends().remove(friend.getId());
    }

    public List<User> getCommonFriends(User user, User otherUser) {
        Map<Integer, User> users = getAllUsers();
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> otherUserFriends = otherUser.getFriends();

        return userFriends
                .stream()
                .filter(otherUserFriends::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }
}
