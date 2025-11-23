package ru.yandex.practicum.filmorate.storage.friendShip;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Qualifier("FriendshipDbStorage")
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbc;

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";

        jdbc.update(sql, userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? and friend_id = ?";

        jdbc.update(sql, userId, friendId);
    }
}
