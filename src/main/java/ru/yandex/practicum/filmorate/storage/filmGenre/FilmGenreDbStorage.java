package ru.yandex.practicum.filmorate.storage.filmGenre;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Qualifier("FilmGenreDbStorage")
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbc;
    private final GenreStorage genreStorage;


    @Override
    public void addFilmGenre(Integer filmId, Integer genreId) {
        Genre genre = genreStorage.getGenreById(genreId);
        if (genre == null) {
            throw new NotFoundException(String.format("Жанр с id %s не найден", genreId));
        }
        String sql = "INSERT INTO film_genres (film_id, genre_id) " +
        "VALUES (?, ?)";

        jdbc.update(sql, filmId, genreId);
    }

    @Override
    public Set<Genre> getAllFilmGenresById(Integer filmId) {
        String sql = "SELECT g.id id, name FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id WHERE film_id = ?";
        List<Genre> genres = jdbc.query(sql, new GenreMapper(), filmId);
        return new LinkedHashSet<>(genres);
    }

    @Override
    public void deleteAllFilmGenresById(Integer filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";

        jdbc.update(sql, filmId);
    }

    @Override
    public Map<Integer, Set<Genre>> getAllFilmGenres(Collection<Film> films) {
        List<Integer> filmIds = films.stream().map(Film::getId).toList();
        String sql = "SELECT fg.film_id film_id, g.id genre_id, g.name name FROM film_genres fg " +
                "LEFT JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id IN (" +
                filmIds.stream().map(String::valueOf).collect(Collectors.joining(",")) +
                ")";

        Map<Integer, Set<Genre>> res = new HashMap<>();

        jdbc.query(sql, rs -> {
            int filmId = rs.getInt("film_id");
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
            res.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
        });

        return res;
    }

    @Override
    public void addFilmGenres(Integer filmId, Collection<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = new ArrayList<>(genres).get(i);
                ps.setInt(1, filmId);
                ps.setInt(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }
}
