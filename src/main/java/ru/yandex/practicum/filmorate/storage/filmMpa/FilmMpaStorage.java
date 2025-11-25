package ru.yandex.practicum.filmorate.storage.filmMpa;

public interface FilmMpaStorage {
    void addFilmMpa(Integer filmId, Integer mpaId);

    void deleteFilmMpaById(Integer filmId);
}
