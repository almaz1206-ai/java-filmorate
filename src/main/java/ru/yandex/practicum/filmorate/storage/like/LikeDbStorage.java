package ru.yandex.practicum.filmorate.storage.like;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Qualifier("LikeDbStorage")
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbc;


    @Override
    public void addLikeToFilm(Integer filmId, Integer userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";

        jdbc.update(sql, filmId, userId);
    }

    @Override
    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

        jdbc.update(sql, filmId, userId);
    }
}
