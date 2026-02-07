package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void filmValidation_shouldFailOnEmptyName() {
        Film film = new Film();
        film.setName(""); // Ошибка: @NotBlank
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now());

        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void userValidation_shouldFailOnFutureBirthday() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1)); // Ошибка: @PastOrPresent

        assertFalse(validator.validate(user).isEmpty());
    }
}
