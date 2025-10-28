package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        checkFilmExists(film.getId());
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        checkFilmExists(id);

        return filmStorage.getFilmById(id);
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        Film film = filmStorage.getFilmById(filmId);

        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
        log.debug("Лайк добавлен фильму {} от пользователя {}", filmId, userId);
    }

    public void removeLikeToFilm(Integer filmId, Integer userId) {

        checkFilmExists(filmId);
        checkUserExists(userId);

        Film film = filmStorage.getFilmById(filmId);

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException(String.format("Лайк от пользователя %s фильму %s", userId, filmId));
        }

        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
        log.debug("Лайк удален по фильму {} от пользователя {}", filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage
                .getAllFilms()
                .stream()
                .sorted((Film film1, Film film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkFilmExists(Integer filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.warn("Фильм с id {} не найден", filmId);
            throw new NotFoundException(String.format("Фильм с id %s не найден", filmId));
        }
    }

    private void checkUserExists(Integer userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id %s не найден", userId));
        }
    }
}
