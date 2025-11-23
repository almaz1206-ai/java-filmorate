package ru.yandex.practicum.filmorate.storage.filmMpa;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

@Component
@AllArgsConstructor
@Qualifier("FilmMpaDbStorage")
public class FilmMpaDbStorage implements FilmMpaStorage {
    private final JdbcTemplate jdbc;
    private final MpaStorage mpaStorage;

    @Override
    public void addFilmMpa(Integer filmId, Integer mpaId) {
        Mpa mpa = mpaStorage.getMpaById(mpaId);

        if (mpa == null) {
            throw new NotFoundException(String.format("MPA рейтинг с id %s не найден", mpaId));
        }

        String sql = "INSERT INTO film_mpas (film_id, mpa_id) VALUES (?, ?)";

        jdbc.update(sql, filmId, mpaId);
    }

    @Override
    public void deleteFilmMpaById(Integer filmId) {
        String sql = "DELETE FROM film_mpas WHERE film_id = ?";

        jdbc.update(sql, filmId);
    }
}
