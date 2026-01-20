package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserValidationTests {

    @Autowired
    private UserController userController;

    @Test
    void testCreateUserWithInvalidEmail() {
        User user = new User();
        user.setLogin("exampleLogin");
        user.setName("Example Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void testCreateUserWithEmptyLogin() {
        User user = new User();
        user.setEmail("example@email.com");
        user.setName("Example Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void testCreateUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("example@email.com");
        user.setLogin("exampleLogin");
        user.setName("Example Name");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }
}
