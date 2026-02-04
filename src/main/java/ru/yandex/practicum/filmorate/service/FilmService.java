package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final Map<Long, Set<Long>> likesFilms = new HashMap<>();
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {
        check(filmId, userId);
        likesFilms.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        check(filmId, userId);
        if (likesFilms.containsKey(filmId)) {
            likesFilms.get(filmId).remove(userId);
        }
    }

    public List<Long> getPopularFilmIds(Set<Long> allFilmIds, int count) {
        return allFilmIds.stream()
                .sorted((id1, id2) -> {
                    int size1 = likesFilms.getOrDefault(id1, Collections.emptySet()).size();
                    int size2 = likesFilms.getOrDefault(id2, Collections.emptySet()).size();
                    return Integer.compare(size2, size1);
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    private void check(Long filmId, Long userId) {
        if (filmStorage.findById(filmId) == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (userStorage.findUserByID(userId) == null) { // Убедитесь, что в UserStorage есть findById
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}
