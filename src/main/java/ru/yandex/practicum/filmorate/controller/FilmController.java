package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

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
    public Film create(@RequestBody Film film) {
        log.info("Добавление фильма: {}", film.getName());
        validate(film);
        film.setId(getNextId());
        filmStorage.addFilm(film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Обновление фильма с id: {}", newFilm.getId());
        if (filmStorage.findById(newFilm.getId()) == null) {
            throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
        }
        validate(newFilm);
        filmStorage.modifyFilm(newFilm);
        return newFilm;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        if (filmStorage.findById(id) == null) {
            throw new NotFoundException("Фильм не найден");
        }
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        if (filmStorage.findById(id) == null) {
            throw new NotFoundException("Фильм не найден");
        }
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") @Positive Integer count) {
        Set<Long> allIds = filmStorage.findAll().stream()
                .map(Film::getId)
                .collect(Collectors.toSet());

        return filmService.getPopularFilmIds(allIds, count).stream()
                .map(filmStorage::findById)
                .collect(Collectors.toList());
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация не пройдена: название фильма пустое.");
            throw new ValidationException("Название не может быть пустым");
        }
        String description = film.getDescription();
        if (description == null || description.isBlank() || description.length() > 200) {
            log.warn("Валидация не пройдена: некорректное описание.");
            throw new ValidationException("Описание не может быть пустым или длиннее 200 символов.");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.warn("Валидация не пройдена: дата релиза {} раньше допустимой.", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Валидация не пройдена: продолжительность фильма {} должна быть больше 0.", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        log.info("Валидация фильма '{}' пройдена успешно.", film.getName());
    }

    private long getNextId() {
        return filmStorage.findAll().stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0) + 1;
    }
}