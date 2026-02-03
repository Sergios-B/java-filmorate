package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {
    void addFilm(Film film);

    void removeFilm(long filmId);

    void modifyFilm(Film updatedFilm);

    Collection<Film> findAll();

    Film findById(long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Set<Long> bestFilms();
}