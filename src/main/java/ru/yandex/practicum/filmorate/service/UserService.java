package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        validateUsersExist(userId1, userId2);
        return userStorage.getCommonFriends(userId1, userId2);
    }

    public Collection<User> getMyFriends(Long id) {
        if (userStorage.findUserByID(id) == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return userStorage.getMyFriends(id);
    }

    private void validateUsersExist(Long... ids) {
        for (Long id : ids) {
            if (userStorage.findUserByID(id) == null) {
                throw new NotFoundException("Пользователь с id " + id + " не найден");
            }
        }
    }
}