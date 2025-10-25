package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.util.Objects;

public class FilmValidator {
    public static boolean isFilmNotFound(Map<Integer, Film> films, Film film) {
        return Objects.isNull(film) || Objects.isNull(films.get(film.getId()));
    }
}
