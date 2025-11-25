package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@Service
@AllArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Genre getGenreById(Integer id) {
        Genre genre = genreStorage.getGenreById(id);
        checkGenreIsNotNull(id);
        return genre;
    }

    public Collection<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    private void checkGenreIsNotNull(Integer genreId) {
        Genre genre = genreStorage.getGenreById(genreId);
        if (genre == null) {
            throw new NotFoundException(String.format("Жанр с id %s не найден", genreId));
        }
    }
}
