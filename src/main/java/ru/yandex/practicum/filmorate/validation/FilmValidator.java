package ru.yandex.practicum.filmorate.validation;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.util.Objects;

@UtilityClass
public class FilmValidator {
    public static boolean isFilmNotFound(Map<Integer, Film> films, Film film) {
        return Objects.isNull(films.get(film.getId()));
    }
}
