package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private static final Logger LOGGER = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
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
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            LOGGER.error("Добавление фильма. Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            LOGGER.error("Добавление фильма. Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        film.setId(getNextId());
        filmStorage.addFilm(film);
        return film;
    }

    private long getNextId() {
        long currentMaxId = filmStorage.findAll().stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (filmStorage.findById(newFilm.getId()) != null) {
            Film oldFilm = filmStorage.findById(newFilm.getId());
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
            filmStorage.modifyFilm(newFilm);
            return newFilm;
        } else {
            LOGGER.error("Обновление данных фильма. Пост с указанным id не найден");
            throw new ValidationException("Пост с указанным id не найден");
        }
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            LOGGER.error("Поиск фильма. Фильм с указанным ID не найден");
            throw new ValidationException("Фильм с указанным ID не найден");
        }
        return film;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        if (filmStorage.findById(filmId) == null){
            LOGGER.error("Новый лайк. Фильм с указанным ID не найден");
            throw new ValidationException("Фильм с указанным ID не найден");
        }
        filmStorage.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        if (filmStorage.findById(filmId) == null){
            LOGGER.error("Удаление лайка. Фильм с указанным ID не найден");
            throw new ValidationException("Фильм с указанным ID не найден");
        }
        filmStorage.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Set<Film> getListFilms(@RequestParam(value = "count", defaultValue = "10") @Positive Integer count) {
        List<Long> filmsId = filmStorage.bestFilms().stream()
                .limit(count)
                .toList();
        Set<Film> filmsBest = new HashSet<>();
        for (Long filmId : filmsId) {
            filmsBest.add(getFilmById(filmId));
        }
        return filmsBest;
    }
}