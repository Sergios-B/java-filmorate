package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        GenreDbStorage.class,
        GenreRowMapper.class
})
@Sql(scripts = "/data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;

    @Test
    public void testFindAll() {
        Collection<Genre> genres = genreStorage.findAll();

        assertThat(genres).isNotEmpty();
        // Проверяем наличие базовых жанров из data.sql
        assertThat(genres).extracting("id").contains(1L, 2L, 3L, 4L, 5L, 6L);
        assertThat(genres).extracting("name").contains(
                "Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
    }

    @Test
    public void testFindById() {
        Optional<Genre> found = genreStorage.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Комедия");
    }

    @Test
    public void testFindByIdNotFound() {
        Optional<Genre> found = genreStorage.findById(999L);
        assertThat(found).isEmpty();
    }
}