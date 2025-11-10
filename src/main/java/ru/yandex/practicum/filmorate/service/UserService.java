package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

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

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Integer id) {
        checkUserIsNotNull(id);

        return userStorage.getUserById(id);
    }

    public List<User> getUserFriends(Integer id) {
        checkUserIsNotNull(id);
        User user = getUserById(id);

        return user
                .getFriends()
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public void addFriendToUser(Integer userId, Integer friendId) {
        checkUserIsNotNull(userId);
        checkUserIsNotNull(friendId);

        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user.getFriends().contains(friend.getId())) {
            throw new ValidationException("Пользователи уже являются друзьями");
        }

        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void deleteFriendFromUser(Integer userId, Integer friendId) {
        checkUserIsNotNull(userId);
        checkUserIsNotNull(friendId);

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        checkUserIsNotNull(userId);
        checkUserIsNotNull(otherUserId);

        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);

        Set<Integer> userFriends = user.getFriends();
        Set<Integer> otherUserFriends = otherUser.getFriends();

        return userFriends
                .stream()
                .filter(otherUserFriends::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    private void checkUserIsNotNull(Integer id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователя с id %s нет", id));
        }
    }
}
