package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    void addFilm(Film film);

    void removeFilm(long filmId);

    void modifyFilm(Film updatedFilm);

    Collection<Film> findAll();

    Film findById(long id);
}