package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

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
        log.info("Добавление фильма: {}", film.getName());
        validateReleaseDate(film);
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновление фильма с id: {}", film.getId());
        validateReleaseDate(film);
        return filmStorage.modifyFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@Valid @PathVariable Long id) {
        Film film = filmStorage.findById(id);
        if (film == null) throw new NotFoundException("Фильм не найден");
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@Valid @PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@Valid @PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@Valid @RequestParam(defaultValue = "10") @Positive Integer count) {
        return filmService.getPopular(count);
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не может быть раньше " + CINEMA_BIRTHDAY);
        }
    }
}