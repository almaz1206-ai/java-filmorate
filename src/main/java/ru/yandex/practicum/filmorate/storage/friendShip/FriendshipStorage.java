package ru.yandex.practicum.filmorate.storage.friendShip;

public interface FriendshipStorage {
    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);
}
