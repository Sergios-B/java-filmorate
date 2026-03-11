package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {

    private static final String FIND_ALL = "SELECT genre_id, name FROM genres ORDER BY genre_id";
    private static final String FIND_BY_ID = "SELECT genre_id, name FROM genres WHERE genre_id = ?";

    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper genreRowMapper) {
        super(jdbcTemplate, genreRowMapper);
    }

    @Override
    public Collection<Genre> findAll() {
        return queryForList(FIND_ALL);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return findOptional(FIND_BY_ID, id);
    }
}