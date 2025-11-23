package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Component
@AllArgsConstructor
@Qualifier("GenreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbc;
    private final String sql = "SELECT * FROM genres";

    @Override
    public Genre getGenreById(Integer genreId) {
        try {
            return jdbc.queryForObject(sql.concat(" WHERE id = ?"), new GenreMapper(), genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Жанр с id %s не найден", genreId));
        }
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbc.query(sql, new GenreMapper());
    }

}
