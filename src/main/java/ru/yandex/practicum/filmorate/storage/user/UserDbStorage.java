package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

@Component
@AllArgsConstructor
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserMapper mapper;

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users (name, login, birthday, email) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getLogin());
            ps.setDate(3, Date.valueOf(user.getBirthday()));
            ps.setString(4, user.getEmail());

            return ps;
        }, keyHolder);

        int userId = Objects.requireNonNull(keyHolder.getKey()).intValue();

        user.setId(userId);

        return user;
    }

    @Override
    public User getUserById(Integer userId) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try {
            return jdbc.queryForObject(sql, mapper, userId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, mapper);
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE id = ?";

        jdbc.update(sql, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());

        return user;
    }

    @Override
    public Collection<User> getUserFriends(Integer userId) {
        String sql = "SELECT * FROM users WHERE id IN (SELECT f.friend_id FROM users u " +
                "JOIN friendships f ON u.id = f.user_id WHERE u.id = ?)";

        return jdbc.query(sql, mapper, userId);
    }

    @Override
    public Collection<User> getCommonFriends(Integer userId, Integer user2Id) {
        String sql = "SELECT * FROM users WHERE id IN (SELECT friend_id FROM friendships WHERE user_id = ?) " +
                "AND id IN (SELECT friend_id FROM friendships WHERE user_id = ?)";
        Collection<User> commonFriends = jdbc.query(sql, mapper, userId, user2Id);
        return commonFriends;
    }
}
