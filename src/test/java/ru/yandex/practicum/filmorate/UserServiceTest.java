package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());
    }

    @Test
    void create_shouldUseLoginIfNameIsEmpty() {
        User user = new User();
        user.setLogin("superstar");
        user.setEmail("test@mail.ru");

        User created = userService.create(user);

        assertEquals("superstar", created.getName(), "Имя должно совпадать с логином");
    }

    @Test
    void addFriend_shouldBeMutual() {
        User user1 = userService.create(new User()); // ID 1
        User user2 = userService.create(new User()); // ID 2

        userService.addFriend(user1.getId(), user2.getId());

        assertTrue(user1.getFriends().contains(user2.getId()));
        assertTrue(user2.getFriends().contains(user1.getId()), "Дружба должна быть взаимной");
    }
}