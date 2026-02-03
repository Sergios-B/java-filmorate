package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final Map<Long, Set<Long>> likesFilms = new HashMap<>();

    public void addLike(Long filmId, Long userId) {
        likesFilms.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (likesFilms.containsKey(filmId)) {
            likesFilms.get(filmId).remove(userId);
        }
    }

    public List<Long> getPopularFilmIds(Set<Long> allFilmIds, int count) {
        return allFilmIds.stream()
                .sorted((id1, id2) -> {
                    int size1 = likesFilms.getOrDefault(id1, Collections.emptySet()).size();
                    int size2 = likesFilms.getOrDefault(id2, Collections.emptySet()).size();
                    return Integer.compare(size2, size1); // Сортировка по убыванию
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}