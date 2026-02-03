package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAllUser() {
        return userStorage.findAllUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            LOGGER.error("Создание пользователя. Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            LOGGER.error("Создание пользователя. Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            LOGGER.error("Создание пользователя. Дата рождения не может быть в будущем");
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        user.setId(getNextId());
        userStorage.addUser(user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = userStorage.getMaxUserId();
        return ++currentMaxId;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        if (userStorage.findUserById(newUser.getId())) {
            if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
                LOGGER.error("Обновление данных пользователя. Электронная почта не может быть пустой и должна содержать символ @");
                throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
            }
            if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
                LOGGER.error("Обновление данных пользователя. Логин не может быть пустым и содержать пробелы");
                throw new ValidationException("логин не может быть пустым и содержать пробелы");
            }
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
            }
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                LOGGER.error("Обновление данных пользователя. Дата рождения не может быть в будущем");
                throw new ValidationException("дата рождения не может быть в будущем");
            }
            userStorage.modifyUser(newUser);
            return newUser;
        } else {
            LOGGER.error("Обновление данных пользователя. Пользователь с указанным id не найден");
            throw new ValidationException("Пользователь с указанным ID не найден");
        }
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        User user = userStorage.findUserByID(id);
        if (user == null) {
            LOGGER.error("Поиск пользователя. Пользователь с указанным id не найден");
            throw new ValidationException("Пользователь с указанным ID не найден");
        }
        return user;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable Long friendId) {
        if (userService.getMyFriends(friendId) != null) {
            LOGGER.info("Добавление друга, который уже есть в друзьях.");
            throw new ValidationException("Пользователь уже у вас в друзьях");
        }
        if (!userStorage.addFriend(id, friendId)) {
            LOGGER.error("Не удалось добавить пользователя в друзья пользователя");
            throw new ValidationException("Не удалось добавить в друзья");
        }
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (!(userStorage.findUserById(id) || userStorage.findUserById(friendId))) {
            LOGGER.error("Удаление друга. Пользователь или друг не найден");
            throw new ValidationException("Не удалось провести операцию");
        }
        userStorage.removeFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        if (!userStorage.findUserById(id)) {
            LOGGER.error("Поиск всех друзей. Пользователь не найден");
            throw new ValidationException("Пользователь с указанным ID не найден");
        }
        return userStorage.getMyFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        if (!userStorage.findUserById(id) || !userStorage.findUserById(otherId)) {
            LOGGER.error("Поиск общих друзей. Пользователь не найден");
            throw new ValidationException("Пользователь не найден");
        }
        return userStorage.getCommonFriends(id, otherId);
    }
}