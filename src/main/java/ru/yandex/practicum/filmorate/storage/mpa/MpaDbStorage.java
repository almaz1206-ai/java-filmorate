package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Component
@AllArgsConstructor
@Qualifier("MpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbc;
    private final MpaMapper mpaMapper;
    private final String sql = "SELECT * FROM mpas";

    @Override
    public Mpa getMpaById(Integer mpaId) {
        try {
            return jdbc.queryForObject(sql.concat(" WHERE id = ?"), mpaMapper, mpaId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        return jdbc.query(sql, mpaMapper);
    }
}
