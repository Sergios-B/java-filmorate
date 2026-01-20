package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        LOGGER.info("Создаётся новый фильм");
        if (film.getName() == null || film.getName().isBlank()) {
            LOGGER.error("Добавление фильма. Название фильма не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().isBlank() || film.getDescription().length() > 200) {
            LOGGER.error("Добавление фильма. Описание не может быть пустым или длиннее 200 символов");
            throw new ValidationException("Описание не может быть пустым или длиннее 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 12)) || film.getReleaseDate().isAfter(LocalDate.now())) {
            LOGGER.error("Добавление фильма. Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            LOGGER.error("Добавление фильма. Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                LOGGER.error("Обновление данных фильма. Название фильма не может быть пустым");
                throw new ValidationException("Название не может быть пустым");
            }
            if (newFilm.getDescription() == null || newFilm.getDescription().isBlank() || newFilm.getDescription().length() > 200) {
                LOGGER.error("Обновление данных фильма. Описание фильма не может быть пустым или длиннее 200 символов");
                throw new ValidationException("Описание не может быть пустым или длиннее 200 символов");
            }
            if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 12))) {
                LOGGER.error("Обновление данных фильма. Дата релиза — не раньше 28 декабря 1895 года");
                throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
            }
            if (newFilm.getDuration() <= 0) {
                LOGGER.error("Обновление данных фильма. Продолжительность фильма должна быть положительным числом");
                throw new ValidationException("Продолжительность фильма должна быть положительным числом");
            }

            films.put(oldFilm.getId(), newFilm);
            return oldFilm;
        } else {
            LOGGER.error("Обновление данных фильма. Пост с id = {} не найден", newFilm.getId());
            throw new ValidationException("Пост с id = " + newFilm.getId() + " не найден");
        }
    }
}