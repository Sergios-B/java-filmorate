package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film addFilm(Film film);

    Film modifyFilm(Film updatedFilm);

    Collection<Film> findAll();

    Film findById(long id);
}
