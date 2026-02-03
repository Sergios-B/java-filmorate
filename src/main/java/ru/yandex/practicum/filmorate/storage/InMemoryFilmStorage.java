package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public void addFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }
    }

    @Override
    public void removeFilm(long filmId) {
        films.remove(filmId);
    }

    @Override
    public void modifyFilm(Film updatedFilm) {
        if (films.containsKey(updatedFilm.getId())) {
            films.put(updatedFilm.getId(), updatedFilm);
        }
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findById(long id) {
        return films.get(id);
    }
}