package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final HashMap<Long, Set<Long>> likesFilms = new HashMap<>();

    public void addLike(Long userId, Long filmId) {
        if (!likesFilms.containsKey(filmId)) {
            likesFilms.put(filmId, new HashSet<>());
        }
        likesFilms.get(filmId).add(userId);
    }

    public void removeLike(Long userId, Long filmId) {
        if (likesFilms.containsKey(filmId)) {
            likesFilms.get(filmId).remove(userId);
        }
    }

    public Set<Long> bestFilms() {
        return likesFilms.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
