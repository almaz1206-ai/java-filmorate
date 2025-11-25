package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmMapper;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.filmMpa.FilmMpaDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@Import({
        FilmDbStorage.class,
        FilmMapper.class,
        FilmMpaDbStorage.class,
        MpaDbStorage.class,
        FilmGenreDbStorage.class,
        GenreDbStorage.class,
        MpaMapper.class,
        GenreMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbcTemplate;

    private Film testFilm;
    private Film savedFilm;

    @BeforeEach
    public void setUp() {
        // Очищаем базу перед каждым тестом
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM film_mpas");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM genres");
        jdbcTemplate.update("DELETE FROM mpas");

        // Заполняем тестовые данные
        jdbcTemplate.update("INSERT INTO mpas (id, name) VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13')");
        jdbcTemplate.update("INSERT INTO genres (id, name) VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм')");

        testFilm = Film.builder()
                .name("Фильм")
                .description("Интересный")
                .releaseDate(LocalDate.now())
                .duration(100)
                .mpa(new Mpa(1, null))
                .rate(5)
                .genres(Set.of(
                        new Genre(1, "Комедия"),
                        new Genre(2, "Драма")
                ))
                .build();

        savedFilm = filmDbStorage.addFilm(testFilm);
    }

    @Test
    public void createFilm() {
        assertThat(savedFilm.getId()).isNotNull();
        assertThat(savedFilm).hasFieldOrPropertyWithValue("name", testFilm.getName());
        assertThat(savedFilm).hasFieldOrPropertyWithValue("description", testFilm.getDescription());
        assertThat(savedFilm).hasFieldOrPropertyWithValue("releaseDate", testFilm.getReleaseDate());
        assertThat(savedFilm).hasFieldOrPropertyWithValue("duration", testFilm.getDuration());
        assertThat(savedFilm).hasFieldOrPropertyWithValue("rate", testFilm.getRate());

        assertEquals(2, savedFilm.getGenres().size());
        Set<String> genreNames = savedFilm.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toSet());
        assertTrue(genreNames.contains("Комедия"));
        assertTrue(genreNames.contains("Драма"));

        assertEquals(testFilm.getMpa().getId(), savedFilm.getMpa().getId());
        assertEquals("G", savedFilm.getMpa().getName());
    }

    @Test
    public void getFilmById() {
        Film filmFromDB = filmDbStorage.getFilmById(savedFilm.getId());

        assertThat(filmFromDB).hasFieldOrPropertyWithValue("id", savedFilm.getId());
        assertThat(filmFromDB).hasFieldOrPropertyWithValue("name", savedFilm.getName());
        assertThat(filmFromDB).hasFieldOrPropertyWithValue("description", savedFilm.getDescription());
        assertThat(filmFromDB).hasFieldOrPropertyWithValue("releaseDate", savedFilm.getReleaseDate());
        assertThat(filmFromDB).hasFieldOrPropertyWithValue("duration", savedFilm.getDuration());
        assertThat(filmFromDB).hasFieldOrPropertyWithValue("rate", savedFilm.getRate());

        assertEquals(2, filmFromDB.getGenres().size());

        Set<String> genreNames = filmFromDB.getGenres().stream()
                .map(Genre::getName)
                .collect(java.util.stream.Collectors.toSet());
        assertTrue(genreNames.contains("Комедия"));
        assertTrue(genreNames.contains("Драма"));

        assertEquals(savedFilm.getMpa().getId(), filmFromDB.getMpa().getId());
        assertEquals("G", filmFromDB.getMpa().getName());
    }

    @Test
    public void getAllFilms() {
        Collection<Film> films = filmDbStorage.getAllFilms();

        assertEquals(1, films.size());

        Film film = films.iterator().next();
        assertEquals(2, film.getGenres().size());
    }

    @Test
    public void updateFilm() {
        Film filmToUpdate = Film.builder()
                .id(savedFilm.getId())
                .name("Фильм!")
                .description(savedFilm.getDescription())
                .releaseDate(savedFilm.getReleaseDate())
                .duration(savedFilm.getDuration())
                .rate(savedFilm.getRate())
                .mpa(savedFilm.getMpa())
                .genres(savedFilm.getGenres())
                .build();

        Film updatedFilm = filmDbStorage.updateFilm(filmToUpdate);

        assertThat(updatedFilm).hasFieldOrPropertyWithValue("id", savedFilm.getId());
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", "Фильм!");
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("description", savedFilm.getDescription());
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("releaseDate", savedFilm.getReleaseDate());
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("duration", savedFilm.getDuration());
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("rate", savedFilm.getRate());
    }
}