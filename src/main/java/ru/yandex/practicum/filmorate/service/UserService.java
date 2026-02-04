package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final Map<Long, Set<Long>> friendships = new HashMap<>();
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        // Проверяем, что оба пользователя существуют в хранилище
        validateUsersExist(userId, friendId);

        friendships.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friendships.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);

        if (friendships.containsKey(userId)) friendships.get(userId).remove(friendId);
        if (friendships.containsKey(friendId)) friendships.get(friendId).remove(userId);
    }

    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        validateUsersExist(userId1, userId2);

        Set<Long> friends1 = friendships.getOrDefault(userId1, Collections.emptySet());
        Set<Long> friends2 = friendships.getOrDefault(userId2, Collections.emptySet());

        Set<Long> commonIds = new HashSet<>(friends1);
        commonIds.retainAll(friends2);

        return commonIds.stream()
                .map(userStorage::findUserByID)
                .collect(Collectors.toList());
    }

    public Collection<User> getMyFriends(Long id) {
        if (userStorage.findUserByID(id) == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }

        return friendships.getOrDefault(id, Collections.emptySet()).stream()
                .map(userStorage::findUserByID)
                .collect(Collectors.toList());
    }

    private void validateUsersExist(Long... ids) {
        for (Long id : ids) {
            if (userStorage.findUserByID(id) == null) {
                throw new NotFoundException("Пользователь с id " + id + " не найден");
            }
        }
    }
}