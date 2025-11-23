package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmMpa.FilmMpaStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmMapper filmMapper;
    private final FilmMpaStorage filmMpaStorage;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, release_date, description, duration, rate)" +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int affectRows = jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setDate(2, Date.valueOf(film.getReleaseDate()));
            ps.setString(3, film.getDescription());
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getRate());
            return ps;
        }, keyHolder);

        if (affectRows == 0) {
            throw new RuntimeException("Не удалось добавить фильм");
        }

        int filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(filmId);

        return addMpaAndGenres(film);
    }

    @Override
    public Film getFilmById(Integer id) {
        try {
            String sql = "SELECT f.*, m.id mpa_id, m.name mpa_name " +
                    "FROM films f " +
                    "LEFT JOIN film_mpas fm ON f.id = fm.film_id " +
                    "LEFT JOIN mpas m on fm.mpa_id = m.id " +
                    "WHERE f.id = ?";

            Film film = jdbc.queryForObject(sql, filmMapper, id);

            if (film != null) {
                Collection<Genre> filmGenres = filmGenreStorage.getAllFilmGenresById(id);
                return film.toBuilder().genres(filmGenres).build();
            }

            return null;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT f.*, m.id mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN film_mpas fm ON f.id = fm.film_id " +
                "LEFT JOIN mpas m on fm.mpa_id = m.id";

        List<Film> films = jdbc.query(sql, filmMapper);

        return setFilmGenres(films);
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, release_date = ?, description = ?, duration = ?, " +
                "rate = ? WHERE id = ?";

        jdbc.update(sql, film.getName(), film.getReleaseDate(), film.getDescription(), film.getDuration(),
                film.getRate(), film.getId());
        filmMpaStorage.deleteFilmMpaById(film.getId());
        filmGenreStorage.deleteAllFilmGenresById(film.getId());

        return addMpaAndGenres(film);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, m.id mpa_id, m.name mpa_name FROM films f " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "LEFT JOIN film_mpas fm ON f.id = fm.film_id " +
                "LEFT JOIN mpas m ON fm.mpa_id = m.id GROUP BY f.name, f.id " +
                "ORDER BY COUNT(l.film_id) DESC LIMIT ?";

        Collection<Film> films = jdbc.query(sql, filmMapper, count);
        return setFilmGenres(films);
    }

    private Collection<Film> setFilmGenres(Collection<Film> films) {
        Map<Integer, Collection<Genre>> filmGenresMap = filmGenreStorage.getAllFilmGenres(films);

        return films.stream().peek(film -> {
            if (Objects.nonNull(filmGenresMap.get(film.getId()))) {
                film.setGenres(filmGenresMap.get(film.getId()));
            }
        }).collect(Collectors.toList());
    }

    private Film addMpaAndGenres(Film film) {
        int filmId = film.getId();
        int mpaId = film.getMpa().getId();

        filmMpaStorage.addFilmMpa(filmId, mpaId);
        new LinkedHashSet<>(film.getGenres()).forEach(genre -> filmGenreStorage.addFilmGenre(filmId, genre.getId()));

        Mpa filmMpa = mpaStorage.getMpaById(mpaId);
        Collection<Genre> filmGenres = filmGenreStorage.getAllFilmGenresById(filmId);

        return film.toBuilder().id(filmId).mpa(filmMpa).genres(filmGenres).build();
    }

}