package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeService likeService;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        checkFilmExists(film.getId());
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        checkFilmExists(id);

        return filmStorage.getFilmById(id);
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        likeService.addLikeToFilm(filmId, userId);
        log.debug("Лайк добавлен фильму {} от пользователя {}", filmId, userId);
    }

    public void deleteLikeFromFilm(Integer filmId, Integer userId) {

        checkFilmExists(filmId);
        checkUserExists(userId);

        likeService.deleteLikeFromFilm(filmId, userId);

        log.debug("Лайк удален по фильму {} от пользователя {}", filmId, userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    private void checkFilmExists(Integer filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.warn("Фильм с id {} не найден", filmId);
            throw new NotFoundException(String.format("Фильм с id %s не найден", filmId));
        }
    }

    private void checkUserExists(Integer userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id %s не найден", userId));
        }
    }
}
