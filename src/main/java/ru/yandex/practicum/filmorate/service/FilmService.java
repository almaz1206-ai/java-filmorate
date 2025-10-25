package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        checkFilmIsNotNull(film, film.getId());
        return filmStorage.updateFilm(film);
    }

    public Map<Integer, Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id);

        checkFilmIsNotNull(film, id);

        return filmStorage.getFilmById(id);
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);

        checkFilmIsNotNull(film, film.getId());
        checkUserExist(userId);

        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public void removeLikeToFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);

        checkFilmIsNotNull(film, film.getId());
        checkUserExist(userId);

        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage
                .getAllFilms()
                .values()
                .stream()
                .sorted((Film film1, Film film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkFilmIsNotNull(Film film, Integer id) {
        if (FilmValidator.isFilmNotFound(getAllFilms(), film)) {
            throw new NotFoundException(String.format("Фильм с id %s не найден", id));
        }
    }

    private void checkUserExist(Integer userId) {
        if (userId <= 0) {
            throw new NotFoundException(String.format("Пользователя с id %s не существует", userId));
        }
    }
}
