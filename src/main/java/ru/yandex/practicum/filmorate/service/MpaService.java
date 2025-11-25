package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Slf4j
@Service
@AllArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public Mpa getMpaById(Integer id) {
        checkMpaExist(id);

        Mpa mpa = mpaStorage.getMpaById(id);

        return mpa;
    }

    public Collection<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }


    private void checkMpaExist(Integer mpaId) {
        Mpa mpa = mpaStorage.getMpaById(mpaId);
        if (mpa == null) {
            log.warn("MPA рейтинг с id {} не найден", mpaId);
            throw new NotFoundException(String.format("MPA рейтинг с id %s не найден", mpaId));
        }
    }
}
