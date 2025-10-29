package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    public static final int DESCRIPTION_MAX_LENGTH = 200;
    private int id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = DESCRIPTION_MAX_LENGTH, message = "Максимальная длина описания - 200 символов")
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
    private Set<Integer> likes = new HashSet<>();
}
