package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

@Service
@AllArgsConstructor
public class LikeService {
    private final LikeStorage likeStorage;

    public void addLikeToFilm(Integer filmId, Integer userId) {
        likeStorage.addLikeToFilm(filmId, userId);
    }

    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        likeStorage.deleteLikeFromFilm(filmId, userId);
    }
}
