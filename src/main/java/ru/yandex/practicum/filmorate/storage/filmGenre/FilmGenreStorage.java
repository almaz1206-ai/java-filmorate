package ru.yandex.practicum.filmorate.storage.filmGenre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface FilmGenreStorage {
    void addFilmGenre(Integer filmId, Integer genreId);

    Set<Genre> getAllFilmGenresById(Integer filmId);

    void deleteAllFilmGenresById(Integer filmId);

    Map<Integer, Set<Genre>> getAllFilmGenres(Collection<Film> films);

    void addFilmGenres(Integer filmId, Collection<Genre> genres);
}
