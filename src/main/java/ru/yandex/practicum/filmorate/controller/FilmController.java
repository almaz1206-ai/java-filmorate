package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма: {}", film.getName());

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен: {} (id: {})", film.getName(), film.getId());

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws NotFoundException {
        log.info("Получен запрос на обновление фильма с ID: {}", film.getId());

        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Такого фильма нет");
        }

        films.put(film.getId(), film);

        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
