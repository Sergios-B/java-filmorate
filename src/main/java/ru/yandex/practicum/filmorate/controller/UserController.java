package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping
    public Collection<User> findAllUser() {
        return userStorage.findAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Создание пользователя: {}", user.getLogin());
        validate(user);
        user.setId(getNextId());
        userStorage.addUser(user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        log.info("Обновление пользователя с id: {}", newUser.getId());
        if (!userStorage.findUserById(newUser.getId())) {
            throw new NotFoundException("Пользователь с id " + newUser.getId() + " не найден");
        }
        validate(newUser);
        userStorage.modifyUser(newUser);
        return newUser;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        User user = userStorage.findUserByID(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (!userStorage.findUserById(id) || !userStorage.findUserById(friendId)) {
            throw new NotFoundException("Пользователь или друг не найден");
        }
        userStorage.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (!userStorage.findUserById(id) || !userStorage.findUserById(friendId)) {
            throw new NotFoundException("Пользователь или друг не найден");
        }
        userStorage.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        if (!userStorage.findUserById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return userStorage.getMyFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        if (!userStorage.findUserById(id) || !userStorage.findUserById(otherId)) {
            throw new NotFoundException("Один из пользователей не найден");
        }
        return userStorage.getCommonFriends(id, otherId);
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Валидация не пройдена: некорректный email '{}'", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Валидация не пройдена: некорректный логин '{}'", user.getLogin());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя пустое, в качестве имени будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Валидация не пройдена: дата рождения {} в будущем", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        log.info("Валидация пользователя '{}' пройдена успешно.", user.getLogin());
    }

    private long getNextId() {
        return userStorage.getMaxUserId() + 1;
    }
}