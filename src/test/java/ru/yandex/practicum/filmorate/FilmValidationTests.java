package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmValidationTests {

    @Autowired
    private FilmController filmController;

    @Test
    void testCreateFilmWithEmptyName() {
        Film film = new Film();
        film.setDescription("Пример описания");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void testCreateFilmWithInvalidDescription() {
        Film film = new Film();
        film.setName("Пример названия");
        film.setDescription("");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void testCreateFilmWithFutureReleaseDate() {
        Film film = new Film();
        film.setName("Пример названия");
        film.setDescription("Пример описания");
        film.setReleaseDate(LocalDate.now().plusDays(1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void testCreateFilmWithInvalidDuration() {
        Film film = new Film();
        film.setName("Пример названия");
        film.setDescription("Пример описания");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(-1);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }
}
